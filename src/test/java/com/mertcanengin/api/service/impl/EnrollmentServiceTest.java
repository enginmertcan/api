package com.mertcanengin.api.service.impl;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.Enrollment;
import com.mertcanengin.api.entity.Lecture;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.entity.enums.EnrollmentStatus;
import com.mertcanengin.api.entity.enums.Role;
import com.mertcanengin.api.repository.IEnrollmentRepository;
import com.mertcanengin.api.repository.ILectureRepository;
import com.mertcanengin.api.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private IEnrollmentRepository enrollmentRepository;
    @Mock
    private ILectureRepository lectureRepository;
    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private Lecture lecture;
    private User student;

    @BeforeEach
    void setUp() {
        lecture = new Lecture();
        lecture.setId(10);
        lecture.setCapacity(2);

        student = new User();
        student.setId(20);
        student.setRole(Role.STUDENT);
    }

    @Test
    void enrollFailsWhenLectureMissing() {
        when(lectureRepository.findById(lecture.getId())).thenReturn(Optional.empty());
        assertThrows(GeneralException.class, () -> enrollmentService.enroll(lecture.getId(), student.getId()));
    }

    @Test
    void enrollFailsWhenCapacityFull() {
        setupHappyPath();
        when(enrollmentRepository.countByLecture_IdAndStatus(lecture.getId(), EnrollmentStatus.ACTIVE))
                .thenReturn(2L);
        assertThrows(GeneralException.class, () -> enrollmentService.enroll(lecture.getId(), student.getId()));
    }

    @Test
    void enrollCreatesNewRecord() {
        setupHappyPath();
        when(enrollmentRepository.countByLecture_IdAndStatus(lecture.getId(), EnrollmentStatus.ACTIVE))
                .thenReturn(1L);
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(inv -> {
            Enrollment e = inv.getArgument(0);
            e.setId(99);
            return e;
        });

        Enrollment created = enrollmentService.enroll(lecture.getId(), student.getId());
        assertEquals(99, created.getId());
        assertEquals(student.getId(), created.getStudent().getId());
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    void enrollReactivatesDroppedRecord() {
        setupHappyPath();
        Enrollment dropped = new Enrollment();
        dropped.setId(1);
        dropped.setLecture(lecture);
        dropped.setStudent(student);
        dropped.setStatus(EnrollmentStatus.DROPPED);
        when(enrollmentRepository.findByLecture_IdAndStudent_Id(lecture.getId(), student.getId()))
                .thenReturn(Optional.of(dropped));
        when(enrollmentRepository.countByLecture_IdAndStatus(lecture.getId(), EnrollmentStatus.ACTIVE))
                .thenReturn(0L);
        when(enrollmentRepository.save(dropped)).thenReturn(dropped);

        Enrollment reactivated = enrollmentService.enroll(lecture.getId(), student.getId());
        assertEquals(EnrollmentStatus.ACTIVE, reactivated.getStatus());
        assertNull(reactivated.getGrade());
    }

    @Test
    void dropFailsWhenCompleted() {
        Enrollment enrollment = activeEnrollment();
        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        when(enrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
        assertThrows(GeneralException.class, () -> enrollmentService.drop(enrollment.getId()));
    }

    @Test
    void dropMarksEnrollment() {
        Enrollment enrollment = activeEnrollment();
        when(enrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.save(enrollment)).thenReturn(enrollment);
        Enrollment dropped = enrollmentService.drop(enrollment.getId());
        assertEquals(EnrollmentStatus.DROPPED, dropped.getStatus());
    }

    @Test
    void completeFailsWithInvalidGrade() {
        Enrollment enrollment = activeEnrollment();
        when(enrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
        assertThrows(GeneralException.class, () -> enrollmentService.complete(enrollment.getId(), 150.0));
    }

    @Test
    void completeMarksAsCompleted() {
        Enrollment enrollment = activeEnrollment();
        when(enrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.save(enrollment)).thenReturn(enrollment);
        Enrollment completed = enrollmentService.complete(enrollment.getId(), 65.0);
        assertEquals(EnrollmentStatus.COMPLETED, completed.getStatus());
        assertEquals(65.0, completed.getGrade());
    }

    @Test
    void pagingDelegatesToRepository() {
        enrollmentService.getAll(PageRequest.of(0, 5));
        verify(enrollmentRepository).findAll(PageRequest.of(0, 5));
    }

    private void setupHappyPath() {
        when(lectureRepository.findById(lecture.getId())).thenReturn(Optional.of(lecture));
        when(userRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(enrollmentRepository.findByLecture_IdAndStudent_Id(lecture.getId(), student.getId()))
                .thenReturn(Optional.empty());
    }

    private Enrollment activeEnrollment() {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(77);
        enrollment.setLecture(lecture);
        enrollment.setStudent(student);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setEnrolledAt(LocalDateTime.now());
        return enrollment;
    }
}
