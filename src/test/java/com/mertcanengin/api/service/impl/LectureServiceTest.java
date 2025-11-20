package com.mertcanengin.api.service.impl;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.domain.lecture.policy.LectureCreationPolicy;
import com.mertcanengin.api.entity.Lecture;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.repository.ILectureRepository;

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
        Mockito.when(lectureCreationPolicy.prepareForSave(lecture)).thenReturn(lecture);
        Mockito.when(lectureRepository.save(lecture)).thenReturn(lecture);

        Lecture saved = lectureService.save(lecture);

        Assertions.assertEquals(lecture, saved);
        Mockito.verify(lectureCreationPolicy).prepareForSave(lecture);
        Mockito.verify(lectureRepository).save(lecture);
    }

    @Test
    void saveThrowsWhenPayloadNull() {
        Assertions.assertThrows(GeneralException.class, () -> lectureService.save(null));
        Mockito.verifyNoInteractions(lectureCreationPolicy, lectureRepository);
    }

    @Test
    void getByIdThrowsWhenMissing() {
        Mockito.when(lectureRepository.findById(10)).thenReturn(Optional.empty());
        Assertions.assertThrows(GeneralException.class, () -> lectureService.getById(10));
    }

    @Test
    void getAllUsesRepositoryPaging() {
        lectureService.getAll(PageRequest.of(0, 5));
        Mockito.verify(lectureRepository).findAll(PageRequest.of(0, 5));
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
