package com.mertcanengin.api.dto;

import com.mertcanengin.api.entity.enums.EnrollmentStatus;

import java.time.LocalDateTime;

public record EnrollmentResponse(
        Integer id,
        Integer lectureId,
        Integer studentId,
        EnrollmentStatus status,
        Double grade,
        Integer waitlistPosition,
        Boolean passed,
        LocalDateTime enrolledAt,
        LocalDateTime approvedAt,
        LocalDateTime completedAt
) {
}
