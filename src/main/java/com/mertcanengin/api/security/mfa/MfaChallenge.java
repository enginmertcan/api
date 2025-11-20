package com.mertcanengin.api.security.mfa;

import java.time.LocalDateTime;

public record MfaChallenge(
        String challengeId,
        LocalDateTime expiresAt,
        String channel
) {
}

