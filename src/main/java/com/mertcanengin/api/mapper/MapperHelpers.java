package com.mertcanengin.api.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.mertcanengin.api.entity.Classroom;
import com.mertcanengin.api.entity.GradeComponent;
import com.mertcanengin.api.entity.Lecture;
import com.mertcanengin.api.entity.ScheduleSlot;

public final class MapperHelpers {

    private MapperHelpers() {
    }

    public static Lecture mapLecture(Integer lectureId) {
        if (lectureId == null) {
            return null;
        }
        Lecture lecture = new Lecture();
        lecture.setId(lectureId);
        return lecture;
    }

    public static Classroom mapClassroom(Integer classroomId) {
        if (classroomId == null) {
            return null;
        }
        Classroom classroom = new Classroom();
        classroom.setId(classroomId);
        return classroom;
    }

    public static ScheduleSlot mapScheduleSlot(Integer slotId) {
        if (slotId == null) {
            return null;
        }
        ScheduleSlot slot = new ScheduleSlot();
        slot.setId(slotId);
        return slot;
    }

    public static GradeComponent mapGradeComponent(Integer componentId) {
        if (componentId == null) {
            return null;
        }
        GradeComponent component = new GradeComponent();
        component.setId(componentId);
        return component;
    }

    public static BigDecimal scaledDecimal(Double value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    public static String teacherFullName(Lecture lecture) {
        if (lecture == null || lecture.getTeacher() == null) {
            return null;
        }
        return (lecture.getTeacher().getName() + " " + lecture.getTeacher().getSurname()).trim();
    }

    public static String teacherIdentityNo(Lecture lecture) {
        if (lecture == null || lecture.getTeacher() == null) {
            return null;
        }
        return lecture.getTeacher().getIdentityNo();
    }
}
