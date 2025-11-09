package com.mertcanengin.api.dto.analytics;

public record AnalyticsSummaryResponse(
        long totalLectures,
        long activeEnrollments,
        long waitlistedEnrollments,
        long classroomsInUse,
        long upcomingSessions
) {
}
