package com.mertcanengin.api.service.impl;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.Lecture;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.entity.enums.Role;
import com.mertcanengin.api.repository.ILectureRepository;
import com.mertcanengin.api.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LectureServiceTest {

    @Mock
    private ILectureRepository lectureRepository;
    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private LectureService lectureService;

    private User teacher;

    @BeforeEach
    void setUp() {
        teacher = new User();
        teacher.setId(5);
        teacher.setRole(Role.TEACHER);
    }

    @Test
    void saveThrowsWhenNameMissing() {
        Lecture lecture = new Lecture();
        lecture.setName(" ");
        lecture.setTeacher(teacher);
        lecture.setCapacity(20);

        assertThrows(GeneralException.class, () -> lectureService.save(lecture));
        verifyNoInteractions(userRepository);
    }

    @Test
    void saveThrowsWhenCapacityInvalid() {
        Lecture lecture = buildLecture();
        lecture.setCapacity(0);
        assertThrows(GeneralException.class, () -> lectureService.save(lecture));
    }

    @Test
    void saveThrowsWhenTeacherNotFound() {
        Lecture lecture = buildLecture();
        when(userRepository.findById(teacher.getId())).thenReturn(Optional.empty());

        GeneralException ex = assertThrows(GeneralException.class, () -> lectureService.save(lecture));
        assertTrue(ex.getMessage().contains("Teacher not found"));
    }

    @Test
    void saveThrowsWhenTeacherIsNotTeacherRole() {
        Lecture lecture = buildLecture();
        teacher.setRole(Role.STUDENT);
        when(userRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));

        GeneralException ex = assertThrows(GeneralException.class, () -> lectureService.save(lecture));
        assertTrue(ex.getMessage().contains("is not a teacher"));
    }

    @Test
    void savePersistsLectureWithResolvedTeacher() {
        Lecture lecture = buildLecture();
        when(userRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));
        when(lectureRepository.save(any(Lecture.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Lecture saved = lectureService.save(lecture);

        ArgumentCaptor<Lecture> captor = ArgumentCaptor.forClass(Lecture.class);
        verify(lectureRepository).save(captor.capture());
        assertEquals(teacher, captor.getValue().getTeacher());
        assertEquals(saved.getName(), captor.getValue().getName());
    }

    @Test
    void getByIdThrowsWhenMissing() {
        when(lectureRepository.findById(10)).thenReturn(Optional.empty());
        assertThrows(GeneralException.class, () -> lectureService.getById(10));
    }

    @Test
    void getAllUsesRepositoryPaging() {
        lectureService.getAll(PageRequest.of(0, 5));
        verify(lectureRepository).findAll(PageRequest.of(0, 5));
    }

    private Lecture buildLecture() {
        Lecture lecture = new Lecture();
        lecture.setName("Advanced Math");
        lecture.setTeacher(teacher);
        lecture.setCapacity(30);
        return lecture;
    }
}
