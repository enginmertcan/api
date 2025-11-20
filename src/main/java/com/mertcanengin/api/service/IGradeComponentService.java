package com.mertcanengin.api.service;

import java.util.List;

import com.mertcanengin.api.entity.GradeComponent;

public interface IGradeComponentService extends IService<GradeComponent> {
    List<GradeComponent> getByLecture(Integer lectureId);
    void validateWeights(Integer lectureId);
}
