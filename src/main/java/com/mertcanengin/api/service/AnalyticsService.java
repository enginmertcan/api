package com.mertcanengin.api.service;

import com.mertcanengin.api.dto.analytics.AnalyticsSummaryResponse;
import com.mertcanengin.api.dto.analytics.EnrollmentFunnelResponse;
import com.mertcanengin.api.dto.analytics.TeacherWorkloadResponse;

import java.util.List;

public interface AnalyticsService {
    AnalyticsSummaryResponse getSummary();

    List<TeacherWorkloadResponse> getTeacherWorkload();

    EnrollmentFunnelResponse getEnrollmentFunnel();
}
