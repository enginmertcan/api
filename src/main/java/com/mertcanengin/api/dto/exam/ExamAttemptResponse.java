package com.mertcanengin.api.dto.exam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.mertcanengin.api.entity.enums.ExamAttemptStatus;

public record ExamAttemptResponse(
        Integer attemptId,
        Integer examId,
        ExamAttemptStatus status,
        BigDecimal score,
        LocalDateTime startedAt,
        LocalDateTime submittedAt,
        List<ExamAnswerResponse> answers
) {
}

