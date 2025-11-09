package com.mertcanengin.api.controller;

import com.mertcanengin.api.dto.analytics.AnalyticsSummaryResponse;
import com.mertcanengin.api.dto.analytics.EnrollmentFunnelResponse;
import com.mertcanengin.api.dto.analytics.TeacherWorkloadResponse;
import com.mertcanengin.api.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    public ResponseEntity<AnalyticsSummaryResponse> getSummary() {
        return ResponseEntity.ok(analyticsService.getSummary());
    }

    @GetMapping("/teacher-workload")
    public ResponseEntity<List<TeacherWorkloadResponse>> getTeacherWorkload() {
        return ResponseEntity.ok(analyticsService.getTeacherWorkload());
    }

    @GetMapping("/enrollment-funnel")
    public ResponseEntity<EnrollmentFunnelResponse> getEnrollmentFunnel() {
        return ResponseEntity.ok(analyticsService.getEnrollmentFunnel());
    }
}
