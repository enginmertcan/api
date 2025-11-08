package com.mertcanengin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record EnrollmentGradeResponse(
        @Schema(example = "77")
        Integer id,
        @Schema(example = "501")
        Integer enrollmentId,
        @Schema(example = "21")
        Integer gradeComponentId,
        @Schema(example = "Vize")
        String componentName,
        @Schema(example = "78.5")
        Double score
) {
}
