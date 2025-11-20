package com.mertcanengin.api.domain.lecture.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.Lecture;

class DefaultLectureValidatorTest {

    private DefaultLectureValidator validator;
    private Lecture lecture;

    @BeforeEach
    void setUp() {
        validator = new DefaultLectureValidator();
        lecture = new Lecture();
        lecture.setName("Biology");
        lecture.setCapacity(30);
    }

    @Test
    void validatePassesForValidLecture() {
        Assertions.assertDoesNotThrow(() -> validator.validate(lecture));
    }

    @Test
    void validateFailsForMissingName() {
        lecture.setName(" ");
        Assertions.assertThrows(GeneralException.class, () -> validator.validate(lecture));
    }

    @Test
    void validateFailsForInvalidCapacity() {
        lecture.setCapacity(0);
        Assertions.assertThrows(GeneralException.class, () -> validator.validate(lecture));
    }
}
