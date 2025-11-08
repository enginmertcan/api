package com.mertcanengin.api.dto;

import com.mertcanengin.api.entity.enums.Gender;
import com.mertcanengin.api.entity.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserResponse(
        @Schema(description = "Kullanıcı ID", example = "42")
        Integer id,

        @Schema(description = "T.C. kimlik numarası", example = "12345678901")
        String identityNo,

        @Schema(example = "Ahmet")
        String name,

        @Schema(example = "Yılmaz")
        String surname,

        @Schema(example = "MALE")
        Gender gender,

        @Schema(example = "STUDENT")
        Role role
) {
}
