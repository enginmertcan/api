package com.mertcanengin.api.mapper;

import com.mertcanengin.api.dto.EnrollmentGradeResponse;
import com.mertcanengin.api.entity.EnrollmentGrade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnrollmentGradeMapper {

    @Mapping(target = "enrollmentId", expression = "java(grade.getEnrollment().getId())")
    @Mapping(target = "gradeComponentId", expression = "java(grade.getGradeComponent().getId())")
    @Mapping(target = "componentName", expression = "java(grade.getGradeComponent().getName())")
    @Mapping(target = "score", expression = "java(grade.getScore() != null ? grade.getScore().doubleValue() : null)")
    EnrollmentGradeResponse toResponse(EnrollmentGrade grade);

    List<EnrollmentGradeResponse> toResponseList(List<EnrollmentGrade> grades);
}
