package com.mertcanengin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public record EnrollmentCompletionRequest(
        @Schema(description = "Final notu", example = "82.5")
        @DecimalMin(value = "0.0", message = "grade must be >= 0")
        @DecimalMax(value = "100.0", message = "grade must be <= 100")
        Double grade
) {
}
