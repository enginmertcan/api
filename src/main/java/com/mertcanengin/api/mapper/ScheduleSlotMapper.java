package com.mertcanengin.api.mapper;

import com.mertcanengin.api.dto.ScheduleSlotRequest;
import com.mertcanengin.api.dto.ScheduleSlotResponse;
import com.mertcanengin.api.entity.ScheduleSlot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScheduleSlotMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lectureSchedules", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    ScheduleSlot toEntity(ScheduleSlotRequest request);

    ScheduleSlotResponse toResponse(ScheduleSlot slot);

    List<ScheduleSlotResponse> toResponseList(List<ScheduleSlot> slots);
}
