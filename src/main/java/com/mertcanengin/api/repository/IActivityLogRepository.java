package com.mertcanengin.api.repository;

import com.mertcanengin.api.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IActivityLogRepository extends JpaRepository<ActivityLog, Long> {
}

