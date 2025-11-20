package com.mertcanengin.api.security.session;

import java.util.Objects;
import java.util.UUID;

public record DeviceMetadata(
        String deviceId,
        String deviceName,
        String ipAddress,
        String userAgent
) {

    public static DeviceMetadata of(String deviceId,
                                    String deviceName,
                                    String ipAddress,
                                    String userAgent) {
        return new DeviceMetadata(
                normalizeDeviceId(deviceId),
                defaultString(deviceName, "Unknown Device"),
                defaultString(ipAddress, "unknown"),
                defaultString(userAgent, "unknown")
        );
    }

    public DeviceMetadata withDeviceId(String newId) {
        return of(newId, deviceName, ipAddress, userAgent);
    }

    private static String normalizeDeviceId(String candidate) {
        if (candidate == null || candidate.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return candidate;
    }

    private static String defaultString(String value, String fallback) {
        return Objects.requireNonNullElse(value, fallback);
    }
}

