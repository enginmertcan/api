package com.mertcanengin.api.dto;

public record EnrollmentGradeResponse(
        Integer id,
        Integer enrollmentId,
        Integer gradeComponentId,
        String componentName,
        Double score
) {
}
