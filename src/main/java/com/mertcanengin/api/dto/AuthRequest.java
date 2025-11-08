package com.mertcanengin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @Schema(description = "T.C. kimlik numarası", example = "12345678901")
        @NotBlank
        String identityNo,

        @Schema(description = "Parola", example = "P@ssw0rd!")
        @NotBlank
        String password
) {
}
