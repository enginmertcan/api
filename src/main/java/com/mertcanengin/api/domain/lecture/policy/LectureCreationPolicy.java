package com.mertcanengin.api.domain.lecture.policy;

import com.mertcanengin.api.entity.Lecture;

public interface LectureCreationPolicy {
    Lecture prepareForSave(Lecture lecture);
}
