package com.mertcanengin.api.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record EnrollmentAttendanceRequest(
        @Schema(description = "Yoklama haftası", example = "2024-04-08")
        @NotNull
        LocalDate weekOf,

        @Schema(description = "Öğrenci derse katıldı mı", example = "true")
        @NotNull
        Boolean attended
) {
}
