package com.mertcanengin.api.dto.analytics;

public record TeacherWorkloadResponse(
        Integer teacherId,
        String teacherName,
        long lectureCount,
        double weeklyHours
) {
}
