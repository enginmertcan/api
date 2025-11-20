package com.mertcanengin.api.entity.common;

import com.mertcanengin.api.service.ActivityLogRecorder;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

public class AuditTrailEntityListener {

    @PostPersist
    public void afterCreate(Object entity) {
        ActivityLogRecorder.record("CREATE", entity);
    }

    @PostUpdate
    public void afterUpdate(Object entity) {
        ActivityLogRecorder.record("UPDATE", entity);
    }

    @PostRemove
    public void afterDelete(Object entity) {
        ActivityLogRecorder.record("DELETE", entity);
    }
}

