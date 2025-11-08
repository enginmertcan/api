package com.mertcanengin.api.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GradeComponentRequest(
        @NotNull
        Integer lectureId,

        @NotBlank
        String name,

        @NotNull
        @DecimalMin(value = "0.1")
        @DecimalMax(value = "100.0")
        Double weight,

        @NotNull
        @DecimalMin(value = "1.0")
        Double maxScore
) {
}
