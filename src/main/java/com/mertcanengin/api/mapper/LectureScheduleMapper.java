package com.mertcanengin.api.mapper;

import com.mertcanengin.api.dto.LectureScheduleRequest;
import com.mertcanengin.api.dto.LectureScheduleResponse;
import com.mertcanengin.api.entity.Classroom;
import com.mertcanengin.api.entity.Lecture;
import com.mertcanengin.api.entity.LectureSchedule;
import com.mertcanengin.api.entity.ScheduleSlot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LectureScheduleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lecture", expression = "java(mapLecture(request.lectureId()))")
    @Mapping(target = "classroom", expression = "java(mapClassroom(request.classroomId()))")
    @Mapping(target = "scheduleSlot", expression = "java(mapSlot(request.scheduleSlotId()))")
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

    default Lecture mapLecture(Integer lectureId) {
        if (lectureId == null) {
            return null;
        }
        Lecture lecture = new Lecture();
        lecture.setId(lectureId);
        return lecture;
    }

    default Classroom mapClassroom(Integer classroomId) {
        if (classroomId == null) {
            return null;
        }
        Classroom classroom = new Classroom();
        classroom.setId(classroomId);
        return classroom;
    }

    default ScheduleSlot mapSlot(Integer slotId) {
        if (slotId == null) {
            return null;
        }
        ScheduleSlot slot = new ScheduleSlot();
        slot.setId(slotId);
        return slot;
    }
}
