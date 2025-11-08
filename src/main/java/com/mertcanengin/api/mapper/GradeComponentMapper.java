package com.mertcanengin.api.mapper;

import com.mertcanengin.api.dto.GradeComponentRequest;
import com.mertcanengin.api.dto.GradeComponentResponse;
import com.mertcanengin.api.entity.GradeComponent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GradeComponentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lecture", expression = "java(com.mertcanengin.api.mapper.MapperHelpers.mapLecture(request.lectureId()))")
    @Mapping(target = "enrollmentGrades", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    GradeComponent toEntity(GradeComponentRequest request);

    @Mapping(target = "lectureId", expression = "java(component.getLecture().getId())")
    GradeComponentResponse toResponse(GradeComponent component);

    List<GradeComponentResponse> toResponseList(List<GradeComponent> components);
}
