package com.mertcanengin.api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.mertcanengin.api.dto.LectureScheduleRequest;
import com.mertcanengin.api.dto.LectureScheduleResponse;
import com.mertcanengin.api.entity.LectureSchedule;

@Mapper(componentModel = "spring")
public interface LectureScheduleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lecture", expression = "java(MapperHelpers.mapLecture(request.lectureId()))")
    @Mapping(target = "classroom", expression = "java(MapperHelpers.mapClassroom(request.classroomId()))")
    @Mapping(target = "scheduleSlot", expression = "java(MapperHelpers.mapScheduleSlot(request.scheduleSlotId()))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    LectureSchedule toEntity(LectureScheduleRequest request);

    @Mapping(target = "lectureId", expression = "java(schedule.getLecture().getId())")
    @Mapping(target = "lectureName", expression = "java(schedule.getLecture().getName())")
    @Mapping(target = "classroomId", expression = "java(schedule.getClassroom().getId())")
    @Mapping(target = "classroomName", expression = "java(schedule.getClassroom().getName())")
    @Mapping(target = "scheduleSlotId", expression = "java(schedule.getScheduleSlot().getId())")
    @Mapping(target = "dayOfWeek", expression = "java(schedule.getScheduleSlot().getDayOfWeek())")
    @Mapping(target = "startTime", expression = "java(schedule.getScheduleSlot().getStartTime())")
    @Mapping(target = "endTime", expression = "java(schedule.getScheduleSlot().getEndTime())")
    LectureScheduleResponse toResponse(LectureSchedule schedule);

    List<LectureScheduleResponse> toResponseList(List<LectureSchedule> schedules);
}
