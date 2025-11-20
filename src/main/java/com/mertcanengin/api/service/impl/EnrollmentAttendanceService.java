package com.mertcanengin.api.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.domain.attendance.AttendancePolicy;
import com.mertcanengin.api.entity.Enrollment;
import com.mertcanengin.api.entity.EnrollmentAttendance;
import com.mertcanengin.api.entity.enums.EnrollmentStatus;
import com.mertcanengin.api.entity.enums.Role;
import com.mertcanengin.api.repository.IEnrollmentAttendanceRepository;
import com.mertcanengin.api.repository.IEnrollmentRepository;
import com.mertcanengin.api.security.SecurityUtils;
import com.mertcanengin.api.service.IEnrollmentAttendanceService;
import com.mertcanengin.api.service.IEnrollmentService;

@Service
public class EnrollmentAttendanceService implements IEnrollmentAttendanceService {

    private final IEnrollmentAttendanceRepository attendanceRepository;
    private final IEnrollmentRepository enrollmentRepository;
    private final IEnrollmentService enrollmentService;

    public EnrollmentAttendanceService(IEnrollmentAttendanceRepository attendanceRepository,
                                       IEnrollmentRepository enrollmentRepository,
                                       IEnrollmentService enrollmentService) {
        this.attendanceRepository = attendanceRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.enrollmentService = enrollmentService;
    }

    @Override
    @Transactional
    public EnrollmentAttendance recordAttendance(Integer enrollmentId, LocalDate weekOf, boolean attended) {
        if (weekOf == null) {
            throw new GeneralException("Hafta bilgisi zorunludur.");
        }

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new GeneralException("Enrollment not found with id: " + enrollmentId));

        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new GeneralException("Yalnızca aktif kayıtlar için yoklama girilebilir.");
        }

        EnrollmentAttendance attendance = attendanceRepository
                .findByEnrollment_IdAndWeekOf(enrollmentId, weekOf)
                .orElseGet(EnrollmentAttendance::new);

        attendance.setEnrollment(enrollment);
        attendance.setWeekOf(weekOf);
        attendance.setAttended(attended);

        EnrollmentAttendance saved = attendanceRepository.save(attendance);

        int absenceCount = (int) attendanceRepository.countByEnrollment_IdAndAttendedFalse(enrollmentId);
        if (enrollment.getAbsenceCount() == null || enrollment.getAbsenceCount() != absenceCount) {
            enrollment.setAbsenceCount(absenceCount);
            enrollmentRepository.save(enrollment);
        }

        if (absenceCount >= AttendancePolicy.MAX_ABSENCE_COUNT
                && enrollment.getStatus() == EnrollmentStatus.ACTIVE) {
            enrollmentService.drop(enrollmentId);
        }

        return saved;
    }

    @Override
    public List<EnrollmentAttendance> getAttendance(Integer enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new GeneralException("Enrollment not found with id: " + enrollmentId));

        boolean isStudent = SecurityUtils.hasRole(Role.STUDENT);
        boolean isPrivileged = SecurityUtils.hasAnyRole(Role.ADMIN, Role.TEACHER);
        if (isStudent && !isPrivileged) {
            Integer currentUserId = SecurityUtils.getCurrentUserId()
                    .orElseThrow(() -> new GeneralException("Kullanıcı bilgisi doğrulanamadı."));
            Integer ownerId = enrollment.getStudentId();
            if (ownerId == null || !ownerId.equals(currentUserId)) {
                throw new GeneralException("Bu yoklamayı görüntüleme yetkin yok.");
            }
        }

        return attendanceRepository.findAllByEnrollment_IdOrderByWeekOfAsc(enrollmentId);
    }
}
