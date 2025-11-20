package com.mertcanengin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record EnrollmentGradeRequest(
        @Schema(description = "Not girilecek bileşenin ID'si", example = "21")
        @NotNull
        Integer gradeComponentId,

        @Schema(description = "Öğrencinin aldığı puan", example = "78.5")
        @NotNull
        @DecimalMin(value = "0.0")
        Double score
) {
}
