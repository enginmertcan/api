package com.mertcanengin.api.dto;

public record GradeComponentResponse(
        Integer id,
        Integer lectureId,
        String name,
        Double weight,
        Double maxScore
) {
}
