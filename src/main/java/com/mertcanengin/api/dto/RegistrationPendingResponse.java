package com.mertcanengin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record RegistrationPendingResponse(
        @Schema(description = "Bilgilendirme mesajı")
        String message,

        @Schema(description = "Doğrulanacak kullanıcının kimlik numarası", example = "12345678901")
        String identityNo,

        @Schema(description = "Doğrulama e-postasının gönderildiği adres", example = "user@example.com")
        String email
) {
}
