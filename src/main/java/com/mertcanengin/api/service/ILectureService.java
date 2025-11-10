package com.mertcanengin.api.service;

import com.mertcanengin.api.entity.Lecture;

import java.util.List;

public interface ILectureService extends IService <Lecture> {
    List<Lecture> getByTeacher(Integer teacherId);
}
