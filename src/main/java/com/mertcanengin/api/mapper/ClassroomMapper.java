package com.mertcanengin.api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.mertcanengin.api.dto.ClassroomRequest;
import com.mertcanengin.api.dto.ClassroomResponse;
import com.mertcanengin.api.entity.Classroom;

@Mapper(componentModel = "spring")
public interface ClassroomMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Classroom toEntity(ClassroomRequest request);

    ClassroomResponse toResponse(Classroom classroom);

    List<ClassroomResponse> toResponseList(List<Classroom> classrooms);
}
