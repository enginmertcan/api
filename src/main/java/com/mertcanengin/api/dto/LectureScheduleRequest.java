package com.mertcanengin.api.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

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
