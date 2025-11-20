package com.mertcanengin.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mertcanengin.api.entity.ScheduleSlot;

@Repository
public interface IScheduleSlotRepository extends JpaRepository<ScheduleSlot, Integer> {
}
