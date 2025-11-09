package com.mertcanengin.api.dto.bootstrap;

public record SampleAccount(
        String role,
        String identityNo,
        String password,
        String description
) {
}
