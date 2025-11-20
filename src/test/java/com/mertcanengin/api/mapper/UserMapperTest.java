package com.mertcanengin.api.mapper;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.mertcanengin.api.dto.RegisterRequest;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.entity.enums.Gender;
import com.mertcanengin.api.entity.enums.Role;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void fromRegisterShouldMapToStudentRoleByDefault() {
        RegisterRequest request =
                new RegisterRequest("12345678901", "Ada", "Lovelace", "ada@example.com", Gender.FEMALE, null, "secret1");

        User user = mapper.fromRegister(request);

        Assertions.assertThat(user.getIdentityNo()).isEqualTo("12345678901");
        Assertions.assertThat(user.getName()).isEqualTo("Ada");
        Assertions.assertThat(user.getSurname()).isEqualTo("Lovelace");
        Assertions.assertThat(user.getGender()).isEqualTo(Gender.FEMALE);
        Assertions.assertThat(user.getPassword()).isEqualTo("secret1");
        Assertions.assertThat(user.getRole()).isEqualTo(Role.STUDENT);
    }

    @Test
    void fromRegisterShouldRespectTeacherRole() {
        RegisterRequest request =
                new RegisterRequest("22222222222", "Alan", "Turing", "alan@example.com", Gender.MALE, Role.TEACHER, "secret2");

        User user = mapper.fromRegister(request);

        Assertions.assertThat(user.getRole()).isEqualTo(Role.TEACHER);
    }
}
