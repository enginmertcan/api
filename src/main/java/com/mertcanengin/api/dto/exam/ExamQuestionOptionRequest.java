package com.mertcanengin.api.dto.exam;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ExamQuestionOptionRequest(
        @Schema(description = "Seçenek metni", example = "Newton yasalarını açıklar.")
        @NotBlank
        String content,

        @Schema(description = "Seçenek etiketi", example = "A")
        String label,

        @Schema(description = "Seçenek sırası", example = "1")
        Integer displayOrder,

        @Schema(description = "Bu seçenek doğru mu?", example = "true")
        boolean correct
) {
}

