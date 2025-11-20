package com.mertcanengin.api.dto.exam;

public record ExamQuestionOptionResponse(
        Integer id,
        String label,
        Integer displayOrder,
        String content,
        boolean correct
) {
}

