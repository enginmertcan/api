package com.mertcanengin.api.dto;

public record ClassroomResponse(
        Integer id,
        String name,
        String location,
        Integer capacity
) {
}
