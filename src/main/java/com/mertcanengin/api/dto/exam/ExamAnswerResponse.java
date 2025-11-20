package com.mertcanengin.api.dto.exam;

import java.math.BigDecimal;

import com.mertcanengin.api.entity.enums.ExamQuestionType;

public record ExamAnswerResponse(
        Integer questionId,
        String prompt,
        ExamQuestionType questionType,
        Integer selectedOptionId,
        String answerText,
        Boolean correct,
        BigDecimal scoreAwarded
) {
}

