package com.mertcanengin.api.dto.exam;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.mertcanengin.api.entity.enums.ExamStatus;

public record ExamSummaryResponse(
        Integer id,
        Integer lectureId,
        String lectureName,
        String title,
        String description,
        LocalDateTime opensAt,
        LocalDateTime closesAt,
        Integer timeLimitMinutes,
        ExamStatus status,
        BigDecimal totalScore
) {
}

