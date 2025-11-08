package com.mertcanengin.api.dto;

public record LectureResponse(
        Integer id,
        String name,
        String description,
        Integer capacity,
        Integer teacherId
) {
}
