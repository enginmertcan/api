package com.mertcanengin.api.domain.lecture.policy;

import com.mertcanengin.api.domain.lecture.teacher.LectureTeacherResolver;
import com.mertcanengin.api.domain.lecture.validation.LectureValidator;
import com.mertcanengin.api.entity.Lecture;
import org.springframework.stereotype.Component;

@Component
public class DefaultLectureCreationPolicy implements LectureCreationPolicy {

    private final LectureValidator validator;
    private final LectureTeacherResolver teacherResolver;

    public DefaultLectureCreationPolicy(LectureValidator validator,
                                        LectureTeacherResolver teacherResolver) {
        this.validator = validator;
        this.teacherResolver = teacherResolver;
    }

    @Override
    public Lecture prepareForSave(Lecture lecture) {
        validator.validate(lecture);
        lecture.setTeacher(teacherResolver.resolve(lecture.getTeacherId()));
        return lecture;
    }
}
