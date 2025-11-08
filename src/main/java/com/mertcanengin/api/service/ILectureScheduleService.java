package com.mertcanengin.api.service;

import com.mertcanengin.api.entity.LectureSchedule;

import java.util.List;

public interface ILectureScheduleService extends IService<LectureSchedule> {
    LectureSchedule schedule(LectureSchedule lectureSchedule);
    List<LectureSchedule> getByLecture(Integer lectureId);
}
