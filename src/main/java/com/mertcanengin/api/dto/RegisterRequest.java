package com.mertcanengin.api.dto;

import com.mertcanengin.api.entity.enums.Gender;
import com.mertcanengin.api.entity.enums.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Schema(description = "T.C. kimlik numarası", example = "12345678901")
        @NotBlank
        @Size(min = 11, max = 11)
        @Pattern(regexp = "\\d{11}", message = "identityNo must contain only digits")
        String identityNo,

        @Schema(description = "Kullanıcının adı", example = "Ayşe")
        @NotBlank
        String name,

        @Schema(description = "Kullanıcının soyadı", example = "Yılmaz")
        @NotBlank
        String surname,

        @Schema(description = "Kullanıcının e-posta adresi", example = "user@example.com")
        @NotBlank
        @Email
        String email,

        @Schema(description = "Cinsiyet bilgisi", example = "FEMALE")
        @NotNull
        Gender gender,

        @Schema(description = "Hesap rolü", example = "STUDENT", defaultValue = "STUDENT")
        Role role,

        @Schema(description = "En az 8 karakterlik parola", example = "Trend123!", minLength = 8)
        @NotBlank
        @Size(min = 8, max = 128)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
                message = "Parola en az bir büyük harf, bir küçük harf, bir rakam ve bir özel karakter içermelidir.")
        String password
) {
}
