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
    @Mapping(target = "weight", expression = "java(new java.math.BigDecimal(request.weight()).setScale(2, java.math.RoundingMode.HALF_UP))")
    @Mapping(target = "maxScore", expression = "java(new java.math.BigDecimal(request.maxScore()).setScale(2, java.math.RoundingMode.HALF_UP))")
    GradeComponent toEntity(GradeComponentRequest request);

    @Mapping(target = "lectureId", expression = "java(component.getLecture().getId())")
    @Mapping(target = "weight", expression = "java(component.getWeight().doubleValue())")
    @Mapping(target = "maxScore", expression = "java(component.getMaxScore().doubleValue())")
    GradeComponentResponse toResponse(GradeComponent component);

    List<GradeComponentResponse> toResponseList(List<GradeComponent> components);
}
