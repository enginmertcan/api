package com.mertcanengin.api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.mertcanengin.api.dto.GradeComponentRequest;
import com.mertcanengin.api.dto.GradeComponentResponse;
import com.mertcanengin.api.entity.GradeComponent;

@Mapper(componentModel = "spring")
public interface GradeComponentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lecture", expression = "java(MapperHelpers.mapLecture(request.lectureId()))")
    @Mapping(target = "enrollmentGrades", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "weight", expression = "java(MapperHelpers.scaledDecimal(request.weight()))")
    @Mapping(target = "maxScore", expression = "java(MapperHelpers.scaledDecimal(request.maxScore()))")
    GradeComponent toEntity(GradeComponentRequest request);

    @Mapping(target = "lectureId", expression = "java(component.getLecture().getId())")
    @Mapping(target = "weight", expression = "java(component.getWeight().doubleValue())")
    @Mapping(target = "maxScore", expression = "java(component.getMaxScore().doubleValue())")
    GradeComponentResponse toResponse(GradeComponent component);

    List<GradeComponentResponse> toResponseList(List<GradeComponent> components);
}
