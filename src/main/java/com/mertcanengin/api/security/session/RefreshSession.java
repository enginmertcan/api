package com.mertcanengin.api.security.session;

import java.time.LocalDateTime;

public record RefreshSession(
        String token,
        Integer userId,
        String identityNo,
        String deviceId,
        String deviceName,
        String ipAddress,
        String userAgent,
        LocalDateTime issuedAt,
        LocalDateTime expiresAt
) {

    public DeviceMetadata deviceMetadata() {
        return DeviceMetadata.of(deviceId, deviceName, ipAddress, userAgent);
    }
}

