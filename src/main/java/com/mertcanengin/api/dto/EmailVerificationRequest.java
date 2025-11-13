package com.mertcanengin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmailVerificationRequest(
        @Schema(description = "T.C. kimlik numarası", example = "12345678901")
        @NotBlank
        @Size(min = 11, max = 11)
        @Pattern(regexp = "\\d{11}", message = "identityNo must contain only digits")
        String identityNo,

        @Schema(description = "E-posta doğrulama kodu", example = "482193")
        @NotBlank
        @Size(min = 6, max = 6)
        String code
) {
}
