package com.mertcanengin.api.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.ScheduleSlot;
import com.mertcanengin.api.repository.IScheduleSlotRepository;
import com.mertcanengin.api.service.IScheduleSlotService;

@Service
public class ScheduleSlotService implements IScheduleSlotService {

    private final IScheduleSlotRepository scheduleSlotRepository;

    public ScheduleSlotService(IScheduleSlotRepository scheduleSlotRepository) {
        this.scheduleSlotRepository = scheduleSlotRepository;
    }

    @Override
    public ScheduleSlot save(ScheduleSlot scheduleSlot) {
        if (scheduleSlot.getStartTime() == null || scheduleSlot.getEndTime() == null) {
            throw new GeneralException("Schedule slot must have a start and end time.");
        }
        if (!scheduleSlot.getStartTime().isBefore(scheduleSlot.getEndTime())) {
            throw new GeneralException("Schedule slot start time must be before end time.");
        }
        if (scheduleSlot.getDayOfWeek() == null) {
            throw new GeneralException("Schedule slot day of week is required.");
        }
        return scheduleSlotRepository.save(scheduleSlot);
    }

    @Override
    public ScheduleSlot getById(Integer id) {
        return scheduleSlotRepository.findById(id)
                .orElseThrow(() -> new GeneralException("Schedule slot not found with id: " + id));
    }

    @Override
    public List<ScheduleSlot> getAll() {
        return scheduleSlotRepository.findAll();
    }

    @Override
    public Page<ScheduleSlot> getAll(Pageable pageable) {
        return scheduleSlotRepository.findAll(pageable);
    }

    @Override
    public void delete(Integer id) {
        if (!scheduleSlotRepository.existsById(id)) {
            throw new GeneralException("Schedule slot not found with id: " + id);
        }
        scheduleSlotRepository.deleteById(id);
    }
}
