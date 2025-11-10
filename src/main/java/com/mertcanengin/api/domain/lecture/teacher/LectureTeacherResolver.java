package com.mertcanengin.api.domain.lecture.teacher;

import com.mertcanengin.api.entity.User;

public interface LectureTeacherResolver {
    User resolve(Integer teacherId);
}
