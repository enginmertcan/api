package com.mertcanengin.api.dto;

import com.mertcanengin.api.entity.enums.Gender;
import com.mertcanengin.api.entity.enums.Role;

public record UserResponse(
        Integer id,
        String identityNo,
        String name,
        String surname,
        Gender gender,
        Role role
) {
}
