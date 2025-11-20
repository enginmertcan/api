package com.mertcanengin.api.service;

import java.util.List;

import com.mertcanengin.api.entity.Lecture;

public interface ILectureService extends IService <Lecture> {
    List<Lecture> getByTeacher(Integer teacherId);
}
