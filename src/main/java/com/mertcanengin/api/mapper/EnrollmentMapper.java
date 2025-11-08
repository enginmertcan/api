package com.mertcanengin.api.mapper;

import com.mertcanengin.api.dto.EnrollmentResponse;
import com.mertcanengin.api.entity.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    @Mapping(target = "lectureId", expression = "java(enrollment.getLectureId())")
    @Mapping(target = "studentId", expression = "java(enrollment.getStudentId())")
    EnrollmentResponse toResponse(Enrollment enrollment);

    List<EnrollmentResponse> toResponseList(List<Enrollment> enrollments);
}
