package com.mertcanengin.api.dto.exam;

import jakarta.validation.constraints.NotNull;

public record ExamAnswerRequest(
        @NotNull
        Integer questionId,
        Integer selectedOptionId,
        String answerText
) {
}

