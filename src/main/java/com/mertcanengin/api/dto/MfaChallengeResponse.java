package com.mertcanengin.api.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

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

