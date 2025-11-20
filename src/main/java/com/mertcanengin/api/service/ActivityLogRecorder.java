package com.mertcanengin.api.service;

import com.mertcanengin.api.entity.common.AuditTrailEntityListener;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogRecorder {

    private final ActivityLogService activityLogService;

    public ActivityLogRecorder(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
        AuditTrailEntityListener.registerRecorder(this);
    }

    public void record(String action, Object entity) {
        if (entity == null) {
            return;
        }
        activityLogService.record(action, entity);
    }
}

