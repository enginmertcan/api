package com.mertcanengin.api.entity.common;

import com.mertcanengin.api.service.ActivityLogRecorder;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

public class AuditTrailEntityListener {

    private static ActivityLogRecorder activityLogRecorder;

    public static void registerRecorder(ActivityLogRecorder recorder) {
        activityLogRecorder = recorder;
    }

    @PostPersist
    public void afterCreate(Object entity) {
        logAction("CREATE", entity);
    }

    @PostUpdate
    public void afterUpdate(Object entity) {
        logAction("UPDATE", entity);
    }

    @PostRemove
    public void afterDelete(Object entity) {
        logAction("DELETE", entity);
    }

    private void logAction(String action, Object entity) {
        if (activityLogRecorder == null || entity == null) {
            return;
        }
        activityLogRecorder.record(action, entity);
    }
}

