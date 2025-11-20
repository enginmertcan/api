package com.mertcanengin.api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.mertcanengin.api.dto.EnrollmentResponse;
import com.mertcanengin.api.entity.Enrollment;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    @Mapping(target = "lectureId", expression = "java(enrollment.getLectureId())")
    @Mapping(target = "studentId", expression = "java(enrollment.getStudentId())")
    @Mapping(target = "grade", expression = "java(enrollment.getFinalGrade() != null ? enrollment.getFinalGrade().doubleValue() : null)")
    @Mapping(target = "absenceLimit", expression = "java(com.mertcanengin.api.domain.attendance.AttendancePolicy.MAX_ABSENCE_COUNT)")
    @Mapping(target = "studentName", expression = "java(enrollment.getStudent() != null ? enrollment.getStudent().getName() : null)")
    @Mapping(target = "studentSurname", expression = "java(enrollment.getStudent() != null ? enrollment.getStudent().getSurname() : null)")
    EnrollmentResponse toResponse(Enrollment enrollment);

    List<EnrollmentResponse> toResponseList(List<Enrollment> enrollments);
}
