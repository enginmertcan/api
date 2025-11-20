package com.mertcanengin.api.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.dto.exam.ExamRequest;
import com.mertcanengin.api.dto.exam.ExamQuestionOptionRequest;
import com.mertcanengin.api.dto.exam.ExamQuestionRequest;
import com.mertcanengin.api.entity.Lecture;
import com.mertcanengin.api.entity.enums.EnrollmentStatus;
import com.mertcanengin.api.entity.enums.ExamQuestionType;
import com.mertcanengin.api.entity.enums.ExamStatus;
import com.mertcanengin.api.entity.enums.Role;
import com.mertcanengin.api.entity.exam.Exam;
import com.mertcanengin.api.mapper.ExamMapper;
import com.mertcanengin.api.repository.IEnrollmentRepository;
import com.mertcanengin.api.repository.IExamRepository;
import com.mertcanengin.api.repository.ILectureRepository;
import com.mertcanengin.api.security.SecurityUtils;
import com.mertcanengin.api.service.IExamService;

@Service
public class ExamService implements IExamService {

    private final IExamRepository examRepository;
    private final ILectureRepository lectureRepository;
    private final IEnrollmentRepository enrollmentRepository;
    private final ExamMapper examMapper;

    public ExamService(IExamRepository examRepository,
                       ILectureRepository lectureRepository,
                       IEnrollmentRepository enrollmentRepository,
                       ExamMapper examMapper) {
        this.examRepository = examRepository;
        this.lectureRepository = lectureRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.examMapper = examMapper;
    }

    @Override
    @Transactional
    public Exam createExam(ExamRequest request) {
        validateRequest(request);
        Lecture lecture = findLecture(request.lectureId());
        ensureTeacherPermission(lecture);

        Exam exam = examMapper.toEntity(request, lecture);
        exam.setTotalScore(calculateTotalScore(exam));
        exam.recalculateStatus(LocalDateTime.now());

        return examRepository.save(exam);
    }

    @Override
    @Transactional
    public Exam updateExam(Integer examId, ExamRequest request) {
        validateRequest(request);
        Exam existing = examRepository.findWithQuestionsById(examId)
                .orElseThrow(() -> new GeneralException("Exam not found with id: " + examId));
        ensureTeacherPermission(existing.getLecture());
        ensureEditable(existing);

        Lecture lecture = findLecture(request.lectureId());
        examMapper.updateEntity(existing, request, lecture);
        existing.setTotalScore(calculateTotalScore(existing));
        existing.recalculateStatus(LocalDateTime.now());

        return examRepository.save(existing);
    }

    @Override
    public Exam getExam(Integer examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new GeneralException("Exam not found with id: " + examId));
        applyTemporalStatus(exam);
        return exam;
    }

    @Override
    public Exam getExamWithQuestions(Integer examId) {
        Exam exam = examRepository.findWithQuestionsById(examId)
                .orElseThrow(() -> new GeneralException("Exam not found with id: " + examId));
        applyTemporalStatus(exam);
        return exam;
    }

    @Override
    public List<Exam> getLectureExams(Integer lectureId) {
        List<Exam> exams = examRepository.findAllByLecture_Id(lectureId);
        exams.forEach(this::applyTemporalStatus);
        return exams;
    }

    @Override
    public List<Exam> getAvailableExams(Integer lectureId) {
        ensureStudentEnrollment(lectureId);
        LocalDateTime now = LocalDateTime.now();
        List<Exam> exams = examRepository
                .findAllByLecture_IdAndOpensAtLessThanEqualAndClosesAtGreaterThan(lectureId, now, now);
        List<Exam> filtered = new ArrayList<>();
        for (Exam exam : exams) {
            exam.recalculateStatus(now);
            if (exam.getStatus() == ExamStatus.ACTIVE) {
                filtered.add(examRepository.save(exam));
            }
        }
        return filtered;
    }

    private Lecture findLecture(Integer lectureId) {
        return lectureRepository.findById(lectureId)
                .orElseThrow(() -> new GeneralException("Lecture not found with id: " + lectureId));
    }

    private void ensureTeacherPermission(Lecture lecture) {
        if (lecture == null) {
            throw new GeneralException("Lecture bilgisi yüklenemedi.");
        }
        Integer teacherId = lecture.getTeacherId();
        boolean isAdmin = SecurityUtils.hasRole(Role.ADMIN);
        boolean isTeacher = SecurityUtils.hasRole(Role.TEACHER);
        if (!isAdmin && !isTeacher) {
            throw new GeneralException("Bu işlem için öğretmen veya admin rolü gerekir.");
        }
        if (isTeacher) {
            Integer currentUserId = SecurityUtils.getCurrentUserId()
                    .orElseThrow(() -> new GeneralException("Kullanıcı kimliği doğrulanamadı."));
            if (!Objects.equals(currentUserId, teacherId)) {
                throw new GeneralException("Bu ders için sınav oluşturma yetkiniz yok.");
            }
        }
    }

    private void ensureEditable(Exam exam) {
        LocalDateTime now = LocalDateTime.now();
        exam.recalculateStatus(now);
        if (EnumSet.of(ExamStatus.ACTIVE, ExamStatus.CLOSED).contains(exam.getStatus())) {
            throw new GeneralException("Başlamış veya tamamlanmış sınav güncellenemez.");
        }
    }

    private void validateRequest(ExamRequest request) {
        if (request == null) {
            throw new GeneralException("Exam request cannot be null.");
        }
        if (request.opensAt().isAfter(request.closesAt())) {
            throw new GeneralException("Sınav başlangıç zamanı bitişten sonra olamaz.");
        }
        if (CollectionUtils.isEmpty(request.questions())) {
            throw new GeneralException("En az bir soru tanımlanmalıdır.");
        }
        for (ExamQuestionRequest question : request.questions()) {
            validateQuestion(question);
        }
    }

    private void validateQuestion(ExamQuestionRequest request) {
        ExamQuestionType type = request.questionType();
        switch (type) {
            case CLASSIC, FILL_IN_THE_BLANK -> {
                if (request.correctAnswer() == null || request.correctAnswer().isBlank()) {
                    throw new GeneralException(
                            "Metin tabanlı sorular için doğru cevap alanı zorunludur: " + request.prompt());
                }
            }
            case MULTIPLE_CHOICE, TRUE_FALSE -> {
                if (CollectionUtils.isEmpty(request.options())) {
                    throw new GeneralException("Seçenekli sorular için seçenek listesi zorunludur: " + request.prompt());
                }
                boolean hasCorrect = request.options().stream().anyMatch(ExamQuestionOptionRequest::correct);
                if (!hasCorrect) {
                    throw new GeneralException("En az bir doğru seçenek işaretlenmelidir: " + request.prompt());
                }
            }
            default -> throw new GeneralException("Desteklenmeyen soru tipi: " + type);
        }
    }

    private BigDecimal calculateTotalScore(Exam exam) {
        return exam.getQuestions().stream()
                .map(question -> question.getPoints() == null ? BigDecimal.ONE : question.getPoints())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void ensureStudentEnrollment(Integer lectureId) {
        if (!SecurityUtils.hasRole(Role.STUDENT)) {
            return;
        }
        Integer userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new GeneralException("Öğrenci bilgisi doğrulanamadı."));
        boolean enrolled = enrollmentRepository
                .existsByLecture_IdAndStudent_IdAndStatus(lectureId, userId, EnrollmentStatus.ACTIVE);
        if (!enrolled) {
            throw new GeneralException("Bu derse kayıtlı değilsiniz.");
        }
    }

    private void applyTemporalStatus(Exam exam) {
        LocalDateTime now = LocalDateTime.now();
        ExamStatus previous = exam.getStatus();
        exam.recalculateStatus(now);
        if (previous != exam.getStatus()) {
            examRepository.save(exam);
        }
    }
}

