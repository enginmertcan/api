package com.mertcanengin.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mertcanengin.api.entity.ActivityLog;

public interface IActivityLogRepository extends JpaRepository<ActivityLog, Long> {
}

