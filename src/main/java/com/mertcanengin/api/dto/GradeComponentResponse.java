package com.mertcanengin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record GradeComponentResponse(
        @Schema(example = "21")
        Integer id,
        @Schema(example = "12")
        Integer lectureId,
        @Schema(example = "Vize")
        String name,
        @Schema(example = "40")
        Double weight,
        @Schema(example = "100")
        Double maxScore
) {
}
