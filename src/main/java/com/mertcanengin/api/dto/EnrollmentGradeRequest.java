package com.mertcanengin.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record EnrollmentGradeRequest(
        @NotNull
        Integer gradeComponentId,

        @NotNull
        @DecimalMin(value = "0.0")
        Double score
) {
}
