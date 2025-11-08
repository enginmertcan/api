package com.mertcanengin.api.dto;

import com.mertcanengin.api.entity.enums.Gender;
import com.mertcanengin.api.entity.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @Schema(description = "T.C. kimlik numarası", example = "12345678901")
        @NotBlank
        @Size(min = 11, max = 11)
        @Pattern(regexp = "\\d{11}", message = "identityNo must contain only digits")
        String identityNo,

        @Schema(description = "Kullanıcının adı", example = "Ahmet")
        @NotBlank
        String name,

        @Schema(description = "Kullanıcının soyadı", example = "Yılmaz")
        @NotBlank
        String surname,

        @Schema(description = "Cinsiyet bilgisi", example = "MALE")
        @NotNull
        Gender gender,

        @Schema(description = "Sistem rolü", example = "STUDENT")
        @NotNull
        Role role,

        @Schema(description = "Kullanıcı parolası", example = "P@ssw0rd!", minLength = 6)
        @NotBlank
        @Size(min = 6, max = 128)
        String password
) {
}
