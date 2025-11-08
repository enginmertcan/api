package com.mertcanengin.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record LectureRequest(
        @NotBlank
        String name,

        @Size(max = 1000)
        String description,

        @NotNull
        @Positive
        Integer capacity,

        @NotNull
        Integer teacherId
) {
}
