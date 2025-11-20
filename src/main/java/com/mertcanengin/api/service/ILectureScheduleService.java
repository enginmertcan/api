package com.mertcanengin.api.service;

import java.util.List;

import com.mertcanengin.api.entity.LectureSchedule;

public interface ILectureScheduleService extends IService<LectureSchedule> {
    LectureSchedule schedule(LectureSchedule lectureSchedule);
    List<LectureSchedule> getByLecture(Integer lectureId);
    List<LectureSchedule> getByLectures(List<Integer> lectureIds);
}
