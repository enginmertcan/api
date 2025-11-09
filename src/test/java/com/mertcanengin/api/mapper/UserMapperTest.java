package com.mertcanengin.api.mapper;

import com.mertcanengin.api.dto.RegisterRequest;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.entity.enums.Gender;
import com.mertcanengin.api.entity.enums.Role;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void fromRegisterShouldMapToStudentRoleByDefault() {
        RegisterRequest request =
                new RegisterRequest("12345678901", "Ada", "Lovelace", Gender.FEMALE, null, "secret1");

        User user = mapper.fromRegister(request);

        assertThat(user.getIdentityNo()).isEqualTo("12345678901");
        assertThat(user.getName()).isEqualTo("Ada");
        assertThat(user.getSurname()).isEqualTo("Lovelace");
        assertThat(user.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(user.getPassword()).isEqualTo("secret1");
        assertThat(user.getRole()).isEqualTo(Role.STUDENT);
    }

    @Test
    void fromRegisterShouldRespectTeacherRole() {
        RegisterRequest request =
                new RegisterRequest("22222222222", "Alan", "Turing", Gender.MALE, Role.TEACHER, "secret2");

        User user = mapper.fromRegister(request);

        assertThat(user.getRole()).isEqualTo(Role.TEACHER);
    }
}
