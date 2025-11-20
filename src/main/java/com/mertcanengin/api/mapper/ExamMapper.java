package com.mertcanengin.api.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.mertcanengin.api.dto.exam.ExamAnswerResponse;
import com.mertcanengin.api.dto.exam.ExamAttemptResponse;
import com.mertcanengin.api.dto.exam.ExamAttemptStartResponse;
import com.mertcanengin.api.dto.exam.ExamPlayQuestionResponse;
import com.mertcanengin.api.dto.exam.ExamPlayResponse;
import com.mertcanengin.api.dto.exam.ExamQuestionOptionRequest;
import com.mertcanengin.api.dto.exam.ExamQuestionOptionResponse;
import com.mertcanengin.api.dto.exam.ExamQuestionOptionView;
import com.mertcanengin.api.dto.exam.ExamQuestionRequest;
import com.mertcanengin.api.dto.exam.ExamQuestionResponse;
import com.mertcanengin.api.dto.exam.ExamRequest;
import com.mertcanengin.api.dto.exam.ExamResponse;
import com.mertcanengin.api.dto.exam.ExamSummaryResponse;
import com.mertcanengin.api.entity.Lecture;
import com.mertcanengin.api.entity.exam.Exam;
import com.mertcanengin.api.entity.exam.ExamAnswer;
import com.mertcanengin.api.entity.exam.ExamAttempt;
import com.mertcanengin.api.entity.exam.ExamQuestion;
import com.mertcanengin.api.entity.exam.ExamQuestionOption;

@Component
public class ExamMapper {

    public Exam toEntity(ExamRequest request, Lecture lecture) {
        Exam exam = new Exam();
        exam.setLecture(lecture);
        applyExamFields(exam, request);
        applyQuestions(exam, request.questions());
        return exam;
    }

    public void updateEntity(Exam existing, ExamRequest request, Lecture lecture) {
        existing.setLecture(lecture);
        applyExamFields(existing, request);
        existing.getQuestions().clear();
        applyQuestions(existing, request.questions());
    }

    private void applyExamFields(Exam exam, ExamRequest request) {
        exam.setTitle(request.title());
        exam.setDescription(request.description());
        exam.setOpensAt(request.opensAt());
        exam.setClosesAt(request.closesAt());
        exam.setTimeLimitMinutes(request.timeLimitMinutes());
    }

    private void applyQuestions(Exam exam, List<ExamQuestionRequest> questionRequests) {
        if (questionRequests == null) {
            return;
        }
        int order = 1;
        for (ExamQuestionRequest questionRequest : questionRequests) {
            ExamQuestion question = new ExamQuestion();
            question.setPrompt(questionRequest.prompt());
            question.setQuestionType(questionRequest.questionType());
            question.setPoints(questionRequest.points());
            question.setCorrectAnswer(questionRequest.correctAnswer());
            question.setQuestionOrder(order++);
            question.setExam(exam);

            List<ExamQuestionOption> options = mapOptionRequests(questionRequest.options(), question);
            question.getOptions().addAll(options);

            exam.getQuestions().add(question);
        }
    }

    private List<ExamQuestionOption> mapOptionRequests(List<ExamQuestionOptionRequest> requests,
                                                       ExamQuestion question) {
        if (requests == null) {
            return List.of();
        }
        List<ExamQuestionOption> options = new ArrayList<>();
        for (ExamQuestionOptionRequest optionRequest : requests) {
            ExamQuestionOption option = new ExamQuestionOption();
            option.setQuestion(question);
            option.setContent(optionRequest.content());
            option.setLabel(optionRequest.label());
            option.setDisplayOrder(optionRequest.displayOrder());
            option.setCorrect(optionRequest.correct());
            options.add(option);
        }
        options.sort(Comparator.comparing(ExamQuestionOption::getDisplayOrder,
                Comparator.nullsLast(Integer::compareTo)));
        return options;
    }

    public ExamResponse toResponse(Exam exam) {
        return new ExamResponse(
                toSummary(exam),
                exam.getQuestions().stream()
                        .sorted(Comparator.comparing(ExamQuestion::getQuestionOrder))
                        .map(this::toQuestionResponse)
                        .toList()
        );
    }

    public ExamSummaryResponse toSummary(Exam exam) {
        Lecture lecture = exam.getLecture();
        return new ExamSummaryResponse(
                exam.getId(),
                lecture != null ? lecture.getId() : null,
                lecture != null ? lecture.getName() : null,
                exam.getTitle(),
                exam.getDescription(),
                exam.getOpensAt(),
                exam.getClosesAt(),
                exam.getTimeLimitMinutes(),
                exam.getStatus(),
                exam.getTotalScore()
        );
    }

    public ExamPlayResponse toPlayResponse(Exam exam) {
        List<ExamPlayQuestionResponse> questions = exam.getQuestions().stream()
                .sorted(Comparator.comparing(ExamQuestion::getQuestionOrder))
                .map(this::toPlayQuestionResponse)
                .toList();
        return new ExamPlayResponse(toSummary(exam), questions);
    }

    public ExamAttemptStartResponse toAttemptStartResponse(ExamAttempt attempt, ExamPlayResponse examResponse) {
        LocalDateTime expiresAt = null;
        if (attempt.getExam().getTimeLimitMinutes() != null) {
            expiresAt = attempt.getStartedAt().plusMinutes(attempt.getExam().getTimeLimitMinutes());
        } else {
            expiresAt = attempt.getExam().getClosesAt();
        }
        return new ExamAttemptStartResponse(
                attempt.getId(),
                attempt.getExam().getId(),
                attempt.getStartedAt(),
                expiresAt,
                examResponse
        );
    }

    public ExamAttemptResponse toAttemptResponse(ExamAttempt attempt) {
        List<ExamAnswerResponse> answers = attempt.getAnswers().stream()
                .map(this::toAnswerResponse)
                .collect(Collectors.toList());
        return new ExamAttemptResponse(
                attempt.getId(),
                attempt.getExam() != null ? attempt.getExam().getId() : null,
                attempt.getStatus(),
                attempt.getScore(),
                attempt.getStartedAt(),
                attempt.getSubmittedAt(),
                answers
        );
    }

    private ExamAnswerResponse toAnswerResponse(ExamAnswer answer) {
        return new ExamAnswerResponse(
                answer.getQuestion() != null ? answer.getQuestion().getId() : null,
                answer.getQuestion() != null ? answer.getQuestion().getPrompt() : null,
                answer.getQuestion() != null ? answer.getQuestion().getQuestionType() : null,
                answer.getSelectedOption() != null ? answer.getSelectedOption().getId() : null,
                answer.getAnswerText(),
                answer.getCorrect(),
                answer.getScoreAwarded()
        );
    }

    private ExamQuestionResponse toQuestionResponse(ExamQuestion question) {
        List<ExamQuestionOptionResponse> options = question.getOptions().stream()
                .sorted(compareOptions())
                .map(option -> new ExamQuestionOptionResponse(
                        option.getId(),
                        option.getLabel(),
                        option.getDisplayOrder(),
                        option.getContent(),
                        option.isCorrect()
                ))
                .toList();
        return new ExamQuestionResponse(
                question.getId(),
                question.getPrompt(),
                question.getQuestionType(),
                defaultPoints(question.getPoints()),
                question.getCorrectAnswer(),
                options
        );
    }

    private ExamPlayQuestionResponse toPlayQuestionResponse(ExamQuestion question) {
        List<ExamQuestionOptionView> options = question.getOptions().stream()
                .sorted(compareOptions())
                .map(option -> new ExamQuestionOptionView(
                        option.getId(),
                        option.getLabel(),
                        option.getDisplayOrder(),
                        option.getContent()
                ))
                .toList();

        boolean textAnswerRequired = switch (question.getQuestionType()) {
            case CLASSIC, FILL_IN_THE_BLANK -> true;
            default -> false;
        };

        return new ExamPlayQuestionResponse(
                question.getId(),
                question.getPrompt(),
                question.getQuestionType(),
                defaultPoints(question.getPoints()),
                textAnswerRequired,
                options
        );
    }

    private Comparator<ExamQuestionOption> compareOptions() {
        return Comparator
                .comparing(ExamQuestionOption::getDisplayOrder, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(option -> Objects.requireNonNullElse(option.getLabel(), ""));
    }

    private BigDecimal defaultPoints(BigDecimal value) {
        return value != null ? value : BigDecimal.ONE;
    }
}

