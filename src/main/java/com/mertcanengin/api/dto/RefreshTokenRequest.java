package com.mertcanengin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @Schema(description = "Önceki refresh token")
        @NotBlank
        String refreshToken
) {
}
