package com.mertcanengin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GradeComponentRequest(
        @Schema(description = "Ders ID", example = "12")
        @NotNull
        Integer lectureId,

        @Schema(description = "Değerlendirme başlığı", example = "Vize")
        @NotBlank
        String name,

        @Schema(description = "Ağırlık yüzdesi", example = "40")
        @NotNull
        @DecimalMin(value = "0.1")
        @DecimalMax(value = "100.0")
        Double weight,

        @Schema(description = "Maksimum puan", example = "100")
        @NotNull
        @DecimalMin(value = "1.0")
        Double maxScore
) {
}
