package com.mertcanengin.api.service.impl;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.domain.lecture.policy.LectureCreationPolicy;
import com.mertcanengin.api.entity.Lecture;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.repository.ILectureRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private LectureCreationPolicy lectureCreationPolicy;

    @InjectMocks
    private LectureService lectureService;

    @Test
    void saveDelegatesToPolicyAndRepository() {
        Lecture lecture = buildLecture();
        when(lectureCreationPolicy.prepareForSave(lecture)).thenReturn(lecture);
        when(lectureRepository.save(lecture)).thenReturn(lecture);

        Lecture saved = lectureService.save(lecture);

        assertEquals(lecture, saved);
        verify(lectureCreationPolicy).prepareForSave(lecture);
        verify(lectureRepository).save(lecture);
    }

    @Test
    void saveThrowsWhenPayloadNull() {
        assertThrows(GeneralException.class, () -> lectureService.save(null));
        verifyNoInteractions(lectureCreationPolicy, lectureRepository);
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
        lecture.setId(1);
        lecture.setTeacher(new User());
        lecture.setName("Advanced Math");
        lecture.setCapacity(30);
        return lecture;
    }
}
