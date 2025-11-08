package com.mertcanengin.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ClassroomRequest(
        @NotBlank
        String name,

        String location,

        @NotNull
        @Positive
        Integer capacity
) {
}
