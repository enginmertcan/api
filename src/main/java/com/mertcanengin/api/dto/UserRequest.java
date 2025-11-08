package com.mertcanengin.api.dto;

import com.mertcanengin.api.entity.enums.Gender;
import com.mertcanengin.api.entity.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank
        @Size(min = 11, max = 11)
        @Pattern(regexp = "\\d{11}", message = "identityNo must contain only digits")
        String identityNo,

        @NotBlank
        String name,

        @NotBlank
        String surname,

        @NotNull
        Gender gender,

        @NotNull
        Role role
) {
}
