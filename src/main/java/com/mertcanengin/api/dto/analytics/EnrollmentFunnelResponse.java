package com.mertcanengin.api.dto.analytics;

import java.util.Map;

public record EnrollmentFunnelResponse(Map<String, Long> statusCounts) {
}
