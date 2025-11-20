package com.mertcanengin.api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public record EnrollmentAttendanceResponse(
        @Schema(example = "10")
        Integer id,

        @Schema(description = "Enrollment ID", example = "501")
        Integer enrollmentId,

        @Schema(description = "Yoklama haftası", example = "2024-04-08")
        LocalDate weekOf,

        @Schema(description = "Öğrencinin derse katılımı", example = "true")
        Boolean attended,

        @Schema(description = "Kaydın oluşturulduğu zaman")
        LocalDateTime recordedAt
) {
}
