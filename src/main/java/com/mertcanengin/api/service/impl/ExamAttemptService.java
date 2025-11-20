package com.mertcanengin.api.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.dto.exam.ExamAnswerRequest;
import com.mertcanengin.api.dto.exam.ExamAttemptSubmitRequest;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.entity.enums.EnrollmentStatus;
import com.mertcanengin.api.entity.enums.ExamAttemptStatus;
import com.mertcanengin.api.entity.enums.Role;
import com.mertcanengin.api.entity.exam.Exam;
import com.mertcanengin.api.entity.exam.ExamAnswer;
import com.mertcanengin.api.entity.exam.ExamAttempt;
import com.mertcanengin.api.entity.exam.ExamQuestion;
import com.mertcanengin.api.entity.exam.ExamQuestionOption;
import com.mertcanengin.api.repository.IEnrollmentRepository;
import com.mertcanengin.api.repository.IExamAnswerRepository;
import com.mertcanengin.api.repository.IExamAttemptRepository;
import com.mertcanengin.api.repository.IExamQuestionOptionRepository;
import com.mertcanengin.api.repository.IUserRepository;
import com.mertcanengin.api.security.SecurityUtils;
import com.mertcanengin.api.service.IExamAttemptService;
import com.mertcanengin.api.service.IExamService;

@Service
public class ExamAttemptService implements IExamAttemptService {

    private final IExamAttemptRepository attemptRepository;
    private final IExamAnswerRepository answerRepository;
    private final IExamQuestionOptionRepository optionRepository;
    private final IUserRepository userRepository;
    private final IEnrollmentRepository enrollmentRepository;
    private final IExamService examService;

    public ExamAttemptService(IExamAttemptRepository attemptRepository,
                              IExamAnswerRepository answerRepository,
                              IExamQuestionOptionRepository optionRepository,
                              IUserRepository userRepository,
                              IEnrollmentRepository enrollmentRepository,
                              IExamService examService) {
        this.attemptRepository = attemptRepository;
        this.answerRepository = answerRepository;
        this.optionRepository = optionRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.examService = examService;
    }

    @Override
    @Transactional
    public ExamAttempt startAttempt(Integer examId) {
        Exam exam = examService.getExamWithQuestions(examId);
        ensureStudentEnrollment(exam);

        LocalDateTime now = LocalDateTime.now();
        if (!exam.isWindowOpen(now)) {
            throw new GeneralException("Sınav henüz aktif değil veya süresi doldu.");
        }

        Integer studentId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new GeneralException("Kullanıcı bilgisi doğrulanamadı."));

        Optional<ExamAttempt> existing = attemptRepository.findByExam_IdAndStudent_Id(examId, studentId);
        if (existing.isPresent()) {
            ExamAttempt attempt = existing.get();
            if (attempt.getStatus() == ExamAttemptStatus.IN_PROGRESS) {
                return attempt;
            }
            throw new GeneralException("Bu sınavı zaten tamamladınız.");
        }

        User studentRef = userRepository.getReferenceById(studentId);

        ExamAttempt attempt = new ExamAttempt();
        attempt.setExam(exam);
        attempt.setStudent(studentRef);
        attempt.setStatus(ExamAttemptStatus.IN_PROGRESS);
        attempt.setStartedAt(now);

        return attemptRepository.save(attempt);
    }

    @Override
    @Transactional
    public ExamAttempt submitAttempt(Integer attemptId, ExamAttemptSubmitRequest request) {
        if (request == null || CollectionUtils.isEmpty(request.answers())) {
            throw new GeneralException("Cevap listesi boş olamaz.");
        }

        ExamAttempt attempt = attemptRepository.findDetailedById(attemptId)
                .orElseThrow(() -> new GeneralException("Sınav oturumu bulunamadı."));

        Exam exam = attempt.getExam();
        ensureStudentEnrollment(exam);

        Integer currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new GeneralException("Kullanıcı bilgisi doğrulanamadı."));

        boolean isTeacherOrAdmin = SecurityUtils.hasAnyRole(Role.ADMIN, Role.TEACHER);
        if (!isTeacherOrAdmin) {
            if (!attempt.getStudent().getId().equals(currentUserId)) {
                throw new GeneralException("Bu sınav oturumuna erişim yetkin yok.");
            }
        }

        if (attempt.getStatus() != ExamAttemptStatus.IN_PROGRESS) {
            throw new GeneralException("Bu sınav oturumu zaten tamamlandı.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(exam.getClosesAt())) {
            throw new GeneralException("Sınav süresi sona erdi.");
        }

        if (exam.getTimeLimitMinutes() != null) {
            LocalDateTime deadline = attempt.getStartedAt().plusMinutes(exam.getTimeLimitMinutes());
            if (now.isAfter(deadline)) {
                throw new GeneralException("Sınav sürenizi aştınız.");
            }
        }

        Map<Integer, ExamQuestion> questionMap = buildQuestionMap(exam);
        validateAnswerRequests(request.answers(), questionMap);

        answerRepository.deleteAllByAttempt_Id(attemptId);
        attempt.getAnswers().clear();

        BigDecimal totalScore = BigDecimal.ZERO;
        for (ExamAnswerRequest answerRequest : request.answers()) {
            ExamQuestion question = questionMap.get(answerRequest.questionId());
            ExamAnswer answer = new ExamAnswer();
            answer.setAttempt(attempt);
            answer.setQuestion(question);

            EvaluationResult evaluationResult = evaluateAnswer(question, answerRequest);
            answer.setSelectedOption(evaluationResult.selectedOption());
            answer.setAnswerText(evaluationResult.answerText());
            answer.setCorrect(evaluationResult.correct());
            answer.setScoreAwarded(evaluationResult.scoreAwarded());
            totalScore = totalScore.add(evaluationResult.scoreAwarded());

            attempt.getAnswers().add(answer);
        }

        attempt.markSubmitted(now, totalScore);
        return attemptRepository.save(attempt);
    }

    @Override
    public ExamAttempt getAttempt(Integer attemptId) {
        return attemptRepository.findDetailedById(attemptId)
                .orElseThrow(() -> new GeneralException("Sınav oturumu bulunamadı."));
    }

    private void ensureStudentEnrollment(Exam exam) {
        Integer studentId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new GeneralException("Kullanıcı kimliği doğrulanamadı."));
        if (SecurityUtils.hasAnyRole(Role.ADMIN, Role.TEACHER)) {
            return;
        }
        boolean enrolled = enrollmentRepository
                .existsByLecture_IdAndStudent_IdAndStatus(exam.getLecture().getId(), studentId, EnrollmentStatus.ACTIVE);
        if (!enrolled) {
            throw new GeneralException("Bu derse kayıtlı değilsiniz.");
        }
    }

    private Map<Integer, ExamQuestion> buildQuestionMap(Exam exam) {
        Map<Integer, ExamQuestion> map = new HashMap<>();
        for (ExamQuestion question : exam.getQuestions()) {
            map.put(question.getId(), question);
        }
        return map;
    }

    private void validateAnswerRequests(Iterable<ExamAnswerRequest> requests,
                                        Map<Integer, ExamQuestion> questionMap) {
        Set<Integer> seenQuestions = new HashSet<>();
        for (ExamAnswerRequest answerRequest : requests) {
            ExamQuestion question = questionMap.get(answerRequest.questionId());
            if (question == null) {
                throw new GeneralException("Geçersiz soru kimliği: " + answerRequest.questionId());
            }
            if (!seenQuestions.add(question.getId())) {
                throw new GeneralException("Aynı soru için birden fazla cevap gönderilemez.");
            }
        }
    }

    private EvaluationResult evaluateAnswer(ExamQuestion question, ExamAnswerRequest answerRequest) {
        BigDecimal questionPoints = question.getPoints() == null ? BigDecimal.ONE : question.getPoints();
        switch (question.getQuestionType()) {
            case MULTIPLE_CHOICE, TRUE_FALSE -> {
                if (answerRequest.selectedOptionId() == null) {
                    throw new GeneralException("Bu soru için bir seçenek seçmelisiniz.");
                }
                ExamQuestionOption option = optionRepository.findByIdAndQuestion_Id(
                        answerRequest.selectedOptionId(), question.getId())
                        .orElseThrow(() -> new GeneralException("Seçenek bulunamadı."));
                boolean correct = option.isCorrect();
                BigDecimal score = correct ? questionPoints : BigDecimal.ZERO;
                return new EvaluationResult(option, null, correct, score);
            }
            case CLASSIC, FILL_IN_THE_BLANK -> {
                String payload = answerRequest.answerText();
                if (!StringUtils.hasText(payload)) {
                    throw new GeneralException("Bu soru için metin cevabı gereklidir.");
                }
                boolean correct = compareTextAnswer(question.getCorrectAnswer(), payload);
                BigDecimal score = correct ? questionPoints : BigDecimal.ZERO;
                return new EvaluationResult(null, payload, correct, score);
            }
            default -> throw new GeneralException("Desteklenmeyen soru tipi.");
        }
    }

    private boolean compareTextAnswer(String expected, String provided) {
        if (!StringUtils.hasText(expected)) {
            return false;
        }
        return expected.trim().equalsIgnoreCase(provided.trim());
    }

    private record EvaluationResult(ExamQuestionOption selectedOption,
                                    String answerText,
                                    boolean correct,
                                    BigDecimal scoreAwarded) {
    }
}

