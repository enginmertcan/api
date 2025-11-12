package com.mertcanengin.api.service.impl;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.domain.attendance.AttendancePolicy;
import com.mertcanengin.api.entity.Enrollment;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.entity.enums.EnrollmentStatus;
import com.mertcanengin.api.entity.enums.Role;
import com.mertcanengin.api.repository.IEnrollmentAttendanceRepository;
import com.mertcanengin.api.repository.IEnrollmentRepository;
import com.mertcanengin.api.security.UserPrincipal;
import com.mertcanengin.api.service.IEnrollmentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentAttendanceServiceTest {

    @Mock
    private IEnrollmentAttendanceRepository attendanceRepository;
    @Mock
    private IEnrollmentRepository enrollmentRepository;
    @Mock
    private IEnrollmentService enrollmentService;

    @InjectMocks
    private EnrollmentAttendanceService attendanceService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void authenticate(Role role, Integer userId) {
        User user = new User();
        user.setId(userId);
        user.setRole(role);
        user.setIdentityNo("id-" + userId);
        user.setPassword("secret");
        UserPrincipal principal = new UserPrincipal(user);
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Test
    void recordAttendanceUpdatesAbsenceCount() {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(1);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);

        when(enrollmentRepository.findById(1)).thenReturn(Optional.of(enrollment));
        when(attendanceRepository.findByEnrollment_IdAndWeekOf(eq(1), any(LocalDate.class)))
                .thenReturn(Optional.empty());
        when(attendanceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(attendanceRepository.countByEnrollment_IdAndAttendedFalse(1)).thenReturn(1L);

        attendanceService.recordAttendance(1, LocalDate.now(), false);

        assertEquals(1, enrollment.getAbsenceCount());
        verify(enrollmentRepository).save(enrollment);
        verify(enrollmentService, never()).drop(anyInt());
    }

    @Test
    void recordAttendanceDropsStudentAtLimit() {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(5);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);

        when(enrollmentRepository.findById(5)).thenReturn(Optional.of(enrollment));
        when(attendanceRepository.findByEnrollment_IdAndWeekOf(eq(5), any(LocalDate.class)))
                .thenReturn(Optional.empty());
        when(attendanceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(attendanceRepository.countByEnrollment_IdAndAttendedFalse(5))
                .thenReturn((long) AttendancePolicy.MAX_ABSENCE_COUNT);

        attendanceService.recordAttendance(5, LocalDate.now(), false);

        verify(enrollmentService).drop(5);
    }

    @Test
    void studentCanViewOwnAttendance() {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(9);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        User student = new User();
        student.setId(42);
        enrollment.setStudent(student);

        authenticate(Role.STUDENT, 42);

        when(enrollmentRepository.findById(9)).thenReturn(Optional.of(enrollment));
        when(attendanceRepository.findAllByEnrollment_IdOrderByWeekOfAsc(9))
                .thenReturn(Collections.emptyList());

        attendanceService.getAttendance(9);

        verify(attendanceRepository).findAllByEnrollment_IdOrderByWeekOfAsc(9);
    }

    @Test
    void studentCannotViewOthersAttendance() {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(15);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        User student = new User();
        student.setId(77);
        enrollment.setStudent(student);

        authenticate(Role.STUDENT, 88);

        when(enrollmentRepository.findById(15)).thenReturn(Optional.of(enrollment));

        assertThrows(GeneralException.class, () -> attendanceService.getAttendance(15));
        verify(attendanceRepository, never()).findAllByEnrollment_IdOrderByWeekOfAsc(anyInt());
    }
}
