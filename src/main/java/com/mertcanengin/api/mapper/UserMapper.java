package com.mertcanengin.api.mapper;

import com.mertcanengin.api.dto.UserRequest;
import com.mertcanengin.api.dto.UserResponse;
import com.mertcanengin.api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toEntity(UserRequest request);

    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);
}
