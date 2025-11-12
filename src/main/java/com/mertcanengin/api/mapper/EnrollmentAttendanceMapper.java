package com.mertcanengin.api.mapper;

import com.mertcanengin.api.dto.EnrollmentAttendanceResponse;
import com.mertcanengin.api.entity.EnrollmentAttendance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnrollmentAttendanceMapper {

    @Mapping(target = "enrollmentId", expression = "java(attendance.getEnrollment() != null ? attendance.getEnrollment().getId() : null)")
    @Mapping(target = "recordedAt", source = "createdAt")
    EnrollmentAttendanceResponse toResponse(EnrollmentAttendance attendance);

    List<EnrollmentAttendanceResponse> toResponseList(List<EnrollmentAttendance> attendanceList);
}
