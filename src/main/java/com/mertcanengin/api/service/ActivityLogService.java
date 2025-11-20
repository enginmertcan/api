package com.mertcanengin.api.service;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mertcanengin.api.entity.ActivityLog;
import com.mertcanengin.api.repository.IActivityLogRepository;

@Service
public class ActivityLogService {

    private final IActivityLogRepository activityLogRepository;
    private final ObjectMapper objectMapper;

    public ActivityLogService(IActivityLogRepository activityLogRepository,
                              ObjectMapper objectMapper) {
        this.activityLogRepository = activityLogRepository;
        this.objectMapper = objectMapper;
    }

    public void record(String action, Object entity) {
        ActivityLog log = new ActivityLog();
        log.setAction(action);
        log.setEntityName(entity.getClass().getSimpleName());
        log.setEntityId(extractId(entity));
        log.setPerformedBy(resolveActor());
        log.setPerformedAt(LocalDateTime.now());
        log.setDetails(serialize(entity));
        activityLogRepository.save(log);
    }

    private String resolveActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "system";
        }
        return authentication.getName();
    }

    private String extractId(Object entity) {
        try {
            Method method = entity.getClass().getMethod("getId");
            Object value = method.invoke(entity);
            return value != null ? value.toString() : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private String serialize(Object entity) {
        try {
            return objectMapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            return entity.toString();
        }
    }
}

