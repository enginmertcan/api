package com.mertcanengin.api.dto;

import java.time.LocalDateTime;

import com.mertcanengin.api.entity.enums.EnrollmentStatus;

import io.swagger.v3.oas.annotations.media.Schema;

public record EnrollmentResponse(
        @Schema(example = "501")
        Integer id,

        @Schema(description = "Ders ID", example = "12")
        Integer lectureId,

        @Schema(description = "Öğrenci ID", example = "1203")
        Integer studentId,

        @Schema(description = "Kayıt durumu", example = "ACTIVE")
        EnrollmentStatus status,

        @Schema(description = "Hesaplanan final notu", example = "78.5")
        Double grade,

        @Schema(description = "Bekleme listesi sırası", example = "2")
        Integer waitlistPosition,

        @Schema(description = "Öğrenci dersten geçti mi", example = "true")
        Boolean passed,

        @Schema(description = "Başvuru zamanı")
        LocalDateTime enrolledAt,

        @Schema(description = "Onay zamanı")
        LocalDateTime approvedAt,

        @Schema(description = "Tamamlama zamanı")
        LocalDateTime completedAt,

        @Schema(description = "Toplam devamsızlık sayısı", example = "2")
        Integer absenceCount,

        @Schema(description = "Devamsızlık limiti", example = "5")
        Integer absenceLimit,

        @Schema(description = "Öğrenci adı", example = "Ayşe")
        String studentName,

        @Schema(description = "Öğrenci soyadı", example = "Yılmaz")
        String studentSurname
) {
}
