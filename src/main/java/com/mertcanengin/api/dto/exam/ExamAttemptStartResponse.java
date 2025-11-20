package com.mertcanengin.api.dto.exam;

import java.time.LocalDateTime;

public record ExamAttemptStartResponse(
        Integer attemptId,
        Integer examId,
        LocalDateTime startedAt,
        LocalDateTime expiresAt,
        ExamPlayResponse exam
) {
}

