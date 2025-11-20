package com.mertcanengin.api.domain.lecture.validation;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.Lecture;

@Component
public class DefaultLectureValidator implements LectureValidator {

    @Override
    public void validate(Lecture lecture) {
        if (!StringUtils.hasText(lecture.getName())) {
            throw new GeneralException("Lecture name cannot be empty.");
        }
        if (lecture.getCapacity() == null || lecture.getCapacity() <= 0) {
            throw new GeneralException("Lecture capacity must be greater than zero.");
        }
    }
}
