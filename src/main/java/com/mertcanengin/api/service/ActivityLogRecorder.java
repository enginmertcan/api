package com.mertcanengin.api.service;

import org.springframework.stereotype.Component;

@Component
public class ActivityLogRecorder {

    private static ActivityLogService delegate;

    public ActivityLogRecorder(ActivityLogService activityLogService) {
        delegate = activityLogService;
    }

    public static void record(String action, Object entity) {
        if (delegate == null || entity == null) {
            return;
        }
        delegate.record(action, entity);
    }
}

