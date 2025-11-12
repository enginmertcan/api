package com.mertcanengin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record EnrollmentAttendanceRequest(
        @Schema(description = "Yoklama haftası", example = "2024-04-08")
        @NotNull
        LocalDate weekOf,

        @Schema(description = "Öğrenci derse katıldı mı", example = "true")
        @NotNull
        Boolean attended
) {
}
