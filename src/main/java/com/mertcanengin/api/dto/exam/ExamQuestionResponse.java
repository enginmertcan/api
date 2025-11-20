package com.mertcanengin.api.dto.exam;

import java.math.BigDecimal;
import java.util.List;

import com.mertcanengin.api.entity.enums.ExamQuestionType;

public record ExamQuestionResponse(
        Integer id,
        String prompt,
        ExamQuestionType questionType,
        BigDecimal points,
        String correctAnswer,
        List<ExamQuestionOptionResponse> options
) {
}

