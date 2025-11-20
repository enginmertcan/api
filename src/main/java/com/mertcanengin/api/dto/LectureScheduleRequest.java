package com.mertcanengin.api.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record LectureScheduleRequest(
        @NotNull
        Integer lectureId,

        @NotNull
        Integer classroomId,

        @NotNull
        Integer scheduleSlotId,

        LocalDate startDate,

        LocalDate endDate
) {
}
