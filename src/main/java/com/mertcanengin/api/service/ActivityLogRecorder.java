package com.mertcanengin.api.service;

import org.springframework.stereotype.Component;

import com.mertcanengin.api.entity.common.AuditTrailEntityListener;

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

