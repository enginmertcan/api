package com.mertcanengin.api.dto;

import jakarta.validation.constraints.NotNull;

public record MfaPreferenceRequest(
        @NotNull
        Boolean enabled
) {
}

