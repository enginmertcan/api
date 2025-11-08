package com.mertcanengin.api.service.impl;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.ScheduleSlot;
import com.mertcanengin.api.repository.IScheduleSlotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        assertThrows(GeneralException.class, () -> scheduleSlotService.save(slot));
    }

    @Test
    void savePersistsWhenValid() {
        ScheduleSlot slot = new ScheduleSlot();
        slot.setDayOfWeek(DayOfWeek.MONDAY);
        slot.setStartTime(LocalTime.of(9, 0));
        slot.setEndTime(LocalTime.of(10, 0));
        when(scheduleSlotRepository.save(slot)).thenReturn(slot);

        scheduleSlotService.save(slot);
        verify(scheduleSlotRepository).save(slot);
    }
}
