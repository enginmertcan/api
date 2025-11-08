package com.mertcanengin.api.mapper;

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
}
