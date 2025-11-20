package com.mertcanengin.api.dto.exam;

public record ExamQuestionOptionView(
        Integer id,
        String label,
        Integer displayOrder,
        String content
) {
}

