package com.mertcanengin.api.domain.lecture.policy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mertcanengin.api.domain.lecture.teacher.LectureTeacherResolver;
import com.mertcanengin.api.domain.lecture.validation.LectureValidator;
import com.mertcanengin.api.entity.Lecture;
import com.mertcanengin.api.entity.User;

@ExtendWith(MockitoExtension.class)
class DefaultLectureCreationPolicyTest {

    @Mock
    private LectureValidator lectureValidator;
    @Mock
    private LectureTeacherResolver lectureTeacherResolver;

    @InjectMocks
    private DefaultLectureCreationPolicy policy;

    private Lecture lecture;
    private User teacher;

    @BeforeEach
    void setUp() {
        lecture = new Lecture();
        lecture.setName("Physics");
        lecture.setCapacity(40);
        lecture.setTeacherId(5);

        teacher = new User();
        teacher.setId(5);
    }

    @Test
    void prepareForSaveValidatesAndAssignsTeacher() {
        Mockito.when(lectureTeacherResolver.resolve(lecture.getTeacherId())).thenReturn(teacher);

        Lecture prepared = policy.prepareForSave(lecture);

        Assertions.assertEquals(teacher, prepared.getTeacher());
        Mockito.verify(lectureValidator).validate(lecture);
        Mockito.verify(lectureTeacherResolver).resolve(lecture.getTeacherId());
    }
}
