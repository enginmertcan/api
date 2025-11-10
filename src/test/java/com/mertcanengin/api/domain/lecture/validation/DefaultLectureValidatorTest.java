package com.mertcanengin.api.domain.lecture.validation;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.Lecture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertDoesNotThrow(() -> validator.validate(lecture));
    }

    @Test
    void validateFailsForMissingName() {
        lecture.setName(" ");
        assertThrows(GeneralException.class, () -> validator.validate(lecture));
    }

    @Test
    void validateFailsForInvalidCapacity() {
        lecture.setCapacity(0);
        assertThrows(GeneralException.class, () -> validator.validate(lecture));
    }
}
