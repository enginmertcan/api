package com.mertcanengin.api.dto.bootstrap;

import java.util.List;

public record SampleDataStatusResponse(
        boolean seeded,
        String note,
        long adminCount,
        long teacherCount,
        long studentCount,
        long lectureCount,
        long classroomCount,
        long scheduleSlotCount,
        long lectureScheduleCount,
        long gradeComponentCount,
        long enrollmentCount,
        long pendingEnrollments,
        long activeEnrollments,
        long waitingEnrollments,
        long completedEnrollments,
        List<SampleAccount> sampleAccounts
) {
}
