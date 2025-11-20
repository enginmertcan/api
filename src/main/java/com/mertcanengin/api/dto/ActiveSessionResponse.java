package com.mertcanengin.api.dto;

import java.time.LocalDateTime;

public record ActiveSessionResponse(
        String deviceId,
        String deviceName,
        String ipAddress,
        String userAgent,
        LocalDateTime issuedAt,
        LocalDateTime expiresAt,
        boolean current
) {
}

