package com.mertcanengin.api.service;

import com.mertcanengin.api.entity.GradeComponent;

import java.util.List;

public interface IGradeComponentService extends IService<GradeComponent> {
    List<GradeComponent> getByLecture(Integer lectureId);
    void validateWeights(Integer lectureId);
}
