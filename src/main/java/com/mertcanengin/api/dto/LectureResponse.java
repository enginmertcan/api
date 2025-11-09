package com.mertcanengin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LectureResponse(
        @Schema(example = "12")
        Integer id,

        @Schema(example = "Bilgisayar Mimarisi")
        String name,

        @Schema(example = "Donanım mimarisi temelleri")
        String description,

        @Schema(example = "40")
        Integer capacity,

        @Schema(description = "Öğretmen ID", example = "5")
        Integer teacherId,

        @Schema(description = "Öğretmen adı soyadı", example = "Ayşe Kaplan")
        String teacherName,

        @Schema(description = "Öğretmenin T.C. kimlik numarası", example = "80000000001")
        String teacherIdentityNo
) {
}
