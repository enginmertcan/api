package com.mertcanengin.api.dto.exam;

import java.math.BigDecimal;
import java.util.List;

import com.mertcanengin.api.entity.enums.ExamQuestionType;

public record ExamPlayQuestionResponse(
        Integer id,
        String prompt,
        ExamQuestionType questionType,
        BigDecimal points,
        boolean textAnswerRequired,
        List<ExamQuestionOptionView> options
) {
}

