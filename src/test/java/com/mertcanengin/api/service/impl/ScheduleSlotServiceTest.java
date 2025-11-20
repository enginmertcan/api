package com.mertcanengin.api.service.impl;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.ScheduleSlot;
import com.mertcanengin.api.repository.IScheduleSlotRepository;

@ExtendWith(MockitoExtension.class)
class ScheduleSlotServiceTest {

    @Mock
    private IScheduleSlotRepository scheduleSlotRepository;

    @InjectMocks
    private ScheduleSlotService scheduleSlotService;

    @Test
    void saveThrowsWhenStartAfterEnd() {
        ScheduleSlot slot = new ScheduleSlot();
        slot.setDayOfWeek(DayOfWeek.MONDAY);
        slot.setStartTime(LocalTime.of(10, 0));
        slot.setEndTime(LocalTime.of(9, 0));

        Assertions.assertThrows(GeneralException.class, () -> scheduleSlotService.save(slot));
    }

    @Test
    void savePersistsWhenValid() {
        ScheduleSlot slot = new ScheduleSlot();
        slot.setDayOfWeek(DayOfWeek.MONDAY);
        slot.setStartTime(LocalTime.of(9, 0));
        slot.setEndTime(LocalTime.of(10, 0));
        Mockito.when(scheduleSlotRepository.save(slot)).thenReturn(slot);

        scheduleSlotService.save(slot);
        Mockito.verify(scheduleSlotRepository).save(slot);
    }
}
