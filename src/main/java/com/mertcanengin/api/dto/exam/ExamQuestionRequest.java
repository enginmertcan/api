package com.mertcanengin.api.dto.exam;

import java.math.BigDecimal;
import java.util.List;

import com.mertcanengin.api.entity.enums.ExamQuestionType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ExamQuestionRequest(
        @Schema(description = "Soru metni", example = "HTTP ile HTTPS arasındaki fark nedir?")
        @NotBlank
        String prompt,

        @Schema(description = "Soru tipi", example = "MULTIPLE_CHOICE")
        @NotNull
        ExamQuestionType questionType,

        @Schema(description = "Soru puanı", example = "10")
        @NotNull
        @DecimalMin(value = "0.1")
        BigDecimal points,

        @Schema(description = "Doğru cevap metni (metin tabanlı soru tipleri için)")
        String correctAnswer,

        @Schema(description = "Seçenekler")
        @Size(max = 10)
        List<@Valid ExamQuestionOptionRequest> options
) {
}

