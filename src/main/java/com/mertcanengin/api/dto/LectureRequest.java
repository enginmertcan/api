package com.mertcanengin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record LectureRequest(
        @Schema(description = "Ders adı", example = "Bilgisayar Mimarisi")
        @NotBlank
        String name,

        @Schema(description = "Kısa açıklama", example = "Donanım mimarisi temelleri")
        @Size(max = 1000)
        String description,

        @Schema(description = "Kontenjan", example = "40")
        @NotNull
        @Positive
        Integer capacity,

        @Schema(description = "Derse atanacak öğretmen ID", example = "5")
        @NotNull
        Integer teacherId
) {
}
