package com.mertcanengin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResponse(
        @Schema(description = "JWT erişim tokeni")
        String accessToken,

        @Schema(description = "Yenileme tokeni")
        String refreshToken,

        @Schema(description = "Oturumu temsil eden cihaz kimliği")
        String deviceId
) {
}
