package com.mertcanengin.api.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.Enrollment;
import com.mertcanengin.api.entity.Lecture;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.entity.enums.EnrollmentStatus;
import com.mertcanengin.api.entity.enums.Role;
import com.mertcanengin.api.repository.IEnrollmentAttendanceRepository;
import com.mertcanengin.api.repository.IEnrollmentRepository;
import com.mertcanengin.api.repository.ILectureRepository;
import com.mertcanengin.api.repository.IUserRepository;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private IEnrollmentRepository enrollmentRepository;
    @Mock
    private ILectureRepository lectureRepository;
    @Mock
    private IUserRepository userRepository;
    @Mock
    private IEnrollmentAttendanceRepository enrollmentAttendanceRepository;

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
        Mockito.when(enrollmentRepository.save(Mockito.any(Enrollment.class))).thenAnswer(inv -> {
            Enrollment e = inv.getArgument(0);
            e.setId(99);
            return e;
        });

        Enrollment enrollment = enrollmentService.enroll(lecture.getId(), student.getId());
        Assertions.assertEquals(99, enrollment.getId());
        Assertions.assertEquals(EnrollmentStatus.PENDING_APPROVAL, enrollment.getStatus());
        Mockito.verify(enrollmentRepository).save(Mockito.any(Enrollment.class));
    }

    @Test
    void reactivatingDroppedEnrollmentResetsAbsence() {
        Mockito.when(lectureRepository.findById(lecture.getId())).thenReturn(Optional.of(lecture));
        Mockito.when(userRepository.findById(student.getId())).thenReturn(Optional.of(student));

        Enrollment dropped = enrollmentPending();
        dropped.setStatus(EnrollmentStatus.DROPPED);
        dropped.setAbsenceCount(3);
        Mockito.when(enrollmentRepository.findByLecture_IdAndStudent_Id(lecture.getId(), student.getId()))
                .thenReturn(Optional.of(dropped));
        Mockito.when(enrollmentRepository.save(dropped)).thenReturn(dropped);

        Enrollment enrollment = enrollmentService.enroll(lecture.getId(), student.getId());

        Assertions.assertEquals(EnrollmentStatus.PENDING_APPROVAL, enrollment.getStatus());
        Assertions.assertEquals(0, enrollment.getAbsenceCount());
        Mockito.verify(enrollmentAttendanceRepository).deleteAllByEnrollment_Id(dropped.getId());
    }

    @Test
    void approveMovesToActiveWhenSeatAvailable() {
        Enrollment pending = enrollmentPending();
        Mockito.when(enrollmentRepository.findById(pending.getId())).thenReturn(Optional.of(pending));
        Mockito.when(enrollmentRepository.countByLecture_IdAndStatus(lecture.getId(), EnrollmentStatus.ACTIVE))
                .thenReturn(0L);
        Mockito.when(enrollmentRepository.save(pending)).thenReturn(pending);

        Enrollment approved = enrollmentService.approve(pending.getId());
        Assertions.assertEquals(EnrollmentStatus.ACTIVE, approved.getStatus());
        Assertions.assertNull(approved.getWaitlistPosition());
    }

    @Test
    void approveMovesToWaitlistWhenFull() {
        Enrollment pending = enrollmentPending();
        Mockito.when(enrollmentRepository.findById(pending.getId())).thenReturn(Optional.of(pending));
        Mockito.when(enrollmentRepository.countByLecture_IdAndStatus(lecture.getId(), EnrollmentStatus.ACTIVE))
                .thenReturn((long) lecture.getCapacity());
        Mockito.when(enrollmentRepository.findAllByLecture_IdAndStatusOrderByWaitlistPositionAsc(
                lecture.getId(), EnrollmentStatus.WAITING)).thenReturn(java.util.Collections.emptyList());
        Mockito.when(enrollmentRepository.save(pending)).thenReturn(pending);

        Enrollment waitlisted = enrollmentService.approve(pending.getId());
        Assertions.assertEquals(EnrollmentStatus.WAITING, waitlisted.getStatus());
        Assertions.assertEquals(1, waitlisted.getWaitlistPosition());
    }

    @Test
    void dropFailsWhenCompleted() {
        Enrollment enrollment = activeEnrollment();
        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        Mockito.when(enrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
        Assertions.assertThrows(GeneralException.class, () -> enrollmentService.drop(enrollment.getId()));
    }

    @Test
    void completeFailsWhenGradeMissing() {
        Enrollment enrollment = activeEnrollment();
        Mockito.when(enrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
        Assertions.assertThrows(GeneralException.class, () -> enrollmentService.complete(enrollment.getId(), null));
    }

    @Test
    void completeUsesProvidedGrade() {
        Enrollment enrollment = activeEnrollment();
        Mockito.when(enrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
        Mockito.when(enrollmentRepository.save(enrollment)).thenReturn(enrollment);
        Enrollment completed = enrollmentService.complete(enrollment.getId(), 85.0);
        Assertions.assertEquals(EnrollmentStatus.COMPLETED, completed.getStatus());
        Assertions.assertEquals(BigDecimal.valueOf(85.0).setScale(2), completed.getFinalGrade());
        Assertions.assertTrue(completed.isPassed());
    }

    @Test
    void pagingDelegatesToRepository() {
        enrollmentService.getAll(PageRequest.of(0, 5));
        Mockito.verify(enrollmentRepository).findAll(PageRequest.of(0, 5));
    }

    private void setupHappyPath() {
        Mockito.when(lectureRepository.findById(lecture.getId())).thenReturn(Optional.of(lecture));
        Mockito.when(userRepository.findById(student.getId())).thenReturn(Optional.of(student));
        Mockito.when(enrollmentRepository.findByLecture_IdAndStudent_Id(lecture.getId(), student.getId()))
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
