package com.mertcanengin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record MfaChallengeResponse(
        @Schema(description = "Kullanıcıya gönderilen challenge kimliği")
        String challengeId,

        @Schema(description = "MFA kodu için son geçerlilik zamanı")
        LocalDateTime expiresAt,

        @Schema(description = "Kodun gönderildiği kanal")
        String channel,

        @Schema(description = "Kullanıcıya gösterilecek mesaj")
        String message
) {
}

