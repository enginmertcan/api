package com.mertcanengin.api.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public record EnrollmentCompletionRequest(
        @DecimalMin(value = "0.0", message = "grade must be >= 0")
        @DecimalMax(value = "100.0", message = "grade must be <= 100")
        Double grade
) {
}
