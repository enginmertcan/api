package com.mertcanengin.api.service;

import java.util.List;

import com.mertcanengin.api.dto.analytics.AnalyticsSummaryResponse;
import com.mertcanengin.api.dto.analytics.EnrollmentFunnelResponse;
import com.mertcanengin.api.dto.analytics.TeacherWorkloadResponse;

public interface AnalyticsService {
    AnalyticsSummaryResponse getSummary();

    List<TeacherWorkloadResponse> getTeacherWorkload();

    EnrollmentFunnelResponse getEnrollmentFunnel();
}
