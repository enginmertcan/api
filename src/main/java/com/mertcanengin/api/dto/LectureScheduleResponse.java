package com.mertcanengin.api.dto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public record LectureScheduleResponse(
        Integer id,
        Integer lectureId,
        String lectureName,
        Integer classroomId,
        String classroomName,
        Integer scheduleSlotId,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        LocalDate startDate,
        LocalDate endDate
) {
}
