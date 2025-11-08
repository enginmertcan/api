package com.mertcanengin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record EnrollmentRequest(
        @Schema(description = "Ders ID", example = "15")
        @NotNull
        Integer lectureId,

        @Schema(description = "Öğrenci ID", example = "1203")
        @NotNull
        Integer studentId
) {
}
