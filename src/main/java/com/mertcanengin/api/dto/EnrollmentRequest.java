package com.mertcanengin.api.dto;

import jakarta.validation.constraints.NotNull;

public record EnrollmentRequest(
        @NotNull
        Integer lectureId,

        @NotNull
        Integer studentId
) {
}
