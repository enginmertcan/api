package com.mertcanengin.api.mapper;

import com.mertcanengin.api.dto.RegisterRequest;
import com.mertcanengin.api.dto.UserRequest;
import com.mertcanengin.api.dto.UserResponse;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.entity.enums.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "emailVerified", expression = "java(request.emailVerified() != null ? request.emailVerified() : Boolean.TRUE)")
    @Mapping(target = "mfaEnabled", ignore = true)
    @Mapping(target = "preferredMfaChannel", ignore = true)
    User toEntity(UserRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "role", expression = "java(resolveRole(request))")
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "mfaEnabled", ignore = true)
    @Mapping(target = "preferredMfaChannel", ignore = true)
    User fromRegister(RegisterRequest request);

    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);

    default Role resolveRole(RegisterRequest request) {
        return request.role() != null ? request.role() : Role.STUDENT;
    }
}
