package com.mertcanengin.api.repository;

import com.mertcanengin.api.entity.ScheduleSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IScheduleSlotRepository extends JpaRepository<ScheduleSlot, Integer> {
}
