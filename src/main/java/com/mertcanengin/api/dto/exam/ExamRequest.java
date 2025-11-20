package com.mertcanengin.api.dto.exam;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ExamRequest(
        @Schema(description = "Ders kimliği", example = "12")
        @NotNull
        Integer lectureId,

        @Schema(description = "Sınav başlığı", example = "Hafta 5 Quiz")
        @NotBlank
        String title,

        @Schema(description = "Açıklama", example = "Orta dönem hazırlık sınavı")
        String description,

        @Schema(description = "Açılış zamanı")
        @NotNull
        LocalDateTime opensAt,

        @Schema(description = "Kapanış zamanı")
        @NotNull
        LocalDateTime closesAt,

        @Schema(description = "Öğrenci başına süre limiti (dakika)")
        @Positive
        Integer timeLimitMinutes,

        @Schema(description = "Sorular")
        @Size(min = 1, message = "En az bir soru eklenmelidir.")
        List<@Valid ExamQuestionRequest> questions
) {
}

