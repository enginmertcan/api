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
    void enrollCreatesPendingRecord() {
        setupHappyPath();
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(inv -> {
            Enrollment e = inv.getArgument(0);
            e.setId(99);
            return e;
        });

        Enrollment enrollment = enrollmentService.enroll(lecture.getId(), student.getId());
        assertEquals(99, enrollment.getId());
        assertEquals(EnrollmentStatus.PENDING_APPROVAL, enrollment.getStatus());
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    void approveMovesToActiveWhenSeatAvailable() {
        Enrollment pending = enrollmentPending();
        when(enrollmentRepository.findById(pending.getId())).thenReturn(Optional.of(pending));
        when(enrollmentRepository.countByLecture_IdAndStatus(lecture.getId(), EnrollmentStatus.ACTIVE))
                .thenReturn(0L);
        when(enrollmentRepository.save(pending)).thenReturn(pending);

        Enrollment approved = enrollmentService.approve(pending.getId());
        assertEquals(EnrollmentStatus.ACTIVE, approved.getStatus());
        assertNull(approved.getWaitlistPosition());
    }

    @Test
    void approveMovesToWaitlistWhenFull() {
        Enrollment pending = enrollmentPending();
        when(enrollmentRepository.findById(pending.getId())).thenReturn(Optional.of(pending));
        when(enrollmentRepository.countByLecture_IdAndStatus(lecture.getId(), EnrollmentStatus.ACTIVE))
                .thenReturn((long) lecture.getCapacity());
        when(enrollmentRepository.findAllByLecture_IdAndStatusOrderByWaitlistPositionAsc(
                lecture.getId(), EnrollmentStatus.WAITING)).thenReturn(java.util.Collections.emptyList());
        when(enrollmentRepository.save(pending)).thenReturn(pending);

        Enrollment waitlisted = enrollmentService.approve(pending.getId());
        assertEquals(EnrollmentStatus.WAITING, waitlisted.getStatus());
        assertEquals(1, waitlisted.getWaitlistPosition());
    }

    @Test
    void dropFailsWhenCompleted() {
        Enrollment enrollment = activeEnrollment();
        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        when(enrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
        assertThrows(GeneralException.class, () -> enrollmentService.drop(enrollment.getId()));
    }

    @Test
    void completeFailsWhenGradeMissing() {
        Enrollment enrollment = activeEnrollment();
        when(enrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
        assertThrows(GeneralException.class, () -> enrollmentService.complete(enrollment.getId(), null));
    }

    @Test
    void completeUsesProvidedGrade() {
        Enrollment enrollment = activeEnrollment();
        when(enrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.save(enrollment)).thenReturn(enrollment);
        Enrollment completed = enrollmentService.complete(enrollment.getId(), 85.0);
        assertEquals(EnrollmentStatus.COMPLETED, completed.getStatus());
        assertEquals(85.0, completed.getFinalGrade());
        assertTrue(completed.isPassed());
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

    private Enrollment enrollmentPending() {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(50);
        enrollment.setLecture(lecture);
        enrollment.setStudent(student);
        enrollment.setStatus(EnrollmentStatus.PENDING_APPROVAL);
        return enrollment;
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
