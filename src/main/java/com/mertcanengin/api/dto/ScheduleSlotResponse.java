package com.mertcanengin.api.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record ScheduleSlotResponse(
        Integer id,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {
}
