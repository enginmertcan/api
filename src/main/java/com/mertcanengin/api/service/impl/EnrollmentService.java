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
import com.mertcanengin.api.service.IEnrollmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnrollmentService implements IEnrollmentService {

    private final IEnrollmentRepository enrollmentRepository;
    private final ILectureRepository lectureRepository;
    private final IUserRepository userRepository;

    public EnrollmentService(IEnrollmentRepository enrollmentRepository,
                             ILectureRepository lectureRepository,
                             IUserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.lectureRepository = lectureRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Enrollment enroll(Integer lectureId, Integer studentId) {
        Lecture lecture = resolveLecture(lectureId);
        User student = resolveStudent(studentId);

        Enrollment existing = enrollmentRepository.findByLecture_IdAndStudent_Id(lectureId, studentId).orElse(null);
        if (existing != null) {
            if (existing.getStatus() == EnrollmentStatus.COMPLETED) {
                throw new GeneralException("Completed enrollment cannot be reactivated.");
            }
            if (existing.getStatus() == EnrollmentStatus.DROPPED) {
                existing.setStatus(EnrollmentStatus.PENDING_APPROVAL);
                existing.setEnrolledAt(LocalDateTime.now());
                existing.setFinalGrade(null);
                existing.setWaitlistPosition(null);
                return enrollmentRepository.save(existing);
            }
            return existing;
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setLecture(lecture);
        enrollment.setStudent(student);
        enrollment.setStatus(EnrollmentStatus.PENDING_APPROVAL);
        enrollment.setEnrolledAt(LocalDateTime.now());
        return enrollmentRepository.save(enrollment);
    }

    @Override
    @Transactional
    public Enrollment approve(Integer enrollmentId) {
        Enrollment enrollment = getById(enrollmentId);
        if (enrollment.getStatus() != EnrollmentStatus.PENDING_APPROVAL) {
            throw new GeneralException("Only pending enrollments can be approved.");
        }
        Lecture lecture = enrollment.getLecture();
        ensureLectureCapacityConfigured(lecture);

        if (hasAvailableSeat(lecture.getId(), lecture.getCapacity())) {
            enrollment.setStatus(EnrollmentStatus.ACTIVE);
            enrollment.setApprovedAt(LocalDateTime.now());
            enrollment.setWaitlistPosition(null);
        } else {
            enrollment.setStatus(EnrollmentStatus.WAITING);
            enrollment.setApprovedAt(LocalDateTime.now());
            enrollment.setWaitlistPosition(nextWaitlistPosition(lecture.getId()));
        }
        return enrollmentRepository.save(enrollment);
    }

    @Override
    @Transactional
    public Enrollment promoteFromWaitlist(Integer enrollmentId) {
        Enrollment enrollment = getById(enrollmentId);
        if (enrollment.getStatus() != EnrollmentStatus.WAITING) {
            throw new GeneralException("Only waitlisted enrollments can be promoted.");
        }
        Lecture lecture = enrollment.getLecture();
        ensureLectureCapacityConfigured(lecture);
        if (!hasAvailableSeat(lecture.getId(), lecture.getCapacity())) {
            throw new GeneralException("No available seats to promote this enrollment.");
        }
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setWaitlistPosition(null);
        enrollment.setApprovedAt(LocalDateTime.now());
        Enrollment saved = enrollmentRepository.save(enrollment);
        reindexWaitlist(lecture.getId());
        return saved;
    }

    @Override
    public Enrollment drop(Integer enrollmentId) {
        Enrollment enrollment = getById(enrollmentId);
        if (enrollment.getStatus() == EnrollmentStatus.DROPPED) {
            throw new GeneralException("Enrollment already dropped.");
        }
        if (enrollment.getStatus() == EnrollmentStatus.COMPLETED) {
            throw new GeneralException("Completed enrollment cannot be dropped.");
        }
        EnrollmentStatus previousStatus = enrollment.getStatus();
        enrollment.setStatus(EnrollmentStatus.DROPPED);
        enrollment.setWaitlistPosition(null);
        Enrollment saved = enrollmentRepository.save(enrollment);
        if (previousStatus == EnrollmentStatus.ACTIVE) {
            promoteNextFromWaitlist(enrollment.getLecture().getId());
        } else {
            reindexWaitlist(enrollment.getLecture().getId());
        }
        return saved;
    }

    @Override
    public Enrollment complete(Integer enrollmentId, Double grade) {
        Enrollment enrollment = getById(enrollmentId);
        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new GeneralException("Only active enrollments can be completed.");
        }
        if (grade != null && (grade < 0 || grade > 100)) {
            throw new GeneralException("Grade must be between 0 and 100.");
        }

        if (grade != null) {
            enrollment.setFinalGrade(BigDecimal.valueOf(grade).setScale(2, RoundingMode.HALF_UP));
        }

        if (enrollment.getFinalGrade() == null) {
            throw new GeneralException("Final grade has not been calculated yet.");
        }

        enrollment.setPassed(enrollment.getFinalGrade().doubleValue() >= 60.0);
        enrollment.setCompletedAt(LocalDateTime.now());
        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        Enrollment saved = enrollmentRepository.save(enrollment);
        promoteNextFromWaitlist(enrollment.getLecture().getId());
        return saved;
    }

    @Override
    public List<Enrollment> getByLecture(Integer lectureId) {
        return enrollmentRepository.findAllByLecture_Id(lectureId);
    }

    @Override
    public List<Enrollment> getByStudent(Integer studentId) {
        return enrollmentRepository.findAllByStudent_Id(studentId);
    }

    @Override
    public Enrollment save(Enrollment enrollment) {
        if (enrollment.getLecture() == null || enrollment.getStudent() == null) {
            throw new GeneralException("Enrollment must contain both lecture and student.");
        }
        return enrollmentRepository.save(enrollment);
    }

    @Override
    public Enrollment getById(Integer id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new GeneralException("Enrollment not found with id: " + id));
    }

    @Override
    public List<Enrollment> getAll() {
        return enrollmentRepository.findAll();
    }

    @Override
    public Page<Enrollment> getAll(Pageable pageable) {
        return enrollmentRepository.findAll(pageable);
    }

    @Override
    public void delete(Integer id) {
        Enrollment enrollment = getById(id);
        EnrollmentStatus previousStatus = enrollment.getStatus();
        Integer lectureId = enrollment.getLecture().getId();
        enrollmentRepository.delete(enrollment);
        if (previousStatus == EnrollmentStatus.ACTIVE) {
            promoteNextFromWaitlist(lectureId);
        } else if (previousStatus == EnrollmentStatus.WAITING) {
            reindexWaitlist(lectureId);
        }
    }

    private Lecture resolveLecture(Integer lectureId) {
        return lectureRepository.findById(lectureId)
                .orElseThrow(() -> new GeneralException("Lecture not found with id: " + lectureId));
    }

    private User resolveStudent(Integer studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new GeneralException("Student not found with id: " + studentId));
        if (student.getRole() != Role.STUDENT) {
            throw new GeneralException("User with id " + studentId + " is not registered as a student.");
        }
        return student;
    }

    private void ensureLectureCapacityConfigured(Lecture lecture) {
        if (lecture.getCapacity() == null || lecture.getCapacity() <= 0) {
            throw new GeneralException("Lecture capacity must be greater than zero.");
        }
    }

    private boolean hasAvailableSeat(Integer lectureId, Integer capacity) {
        long activeCount = enrollmentRepository.countByLecture_IdAndStatus(lectureId, EnrollmentStatus.ACTIVE);
        return activeCount < capacity;
    }

    private int nextWaitlistPosition(Integer lectureId) {
        List<Enrollment> waitlist = enrollmentRepository
                .findAllByLecture_IdAndStatusOrderByWaitlistPositionAsc(lectureId, EnrollmentStatus.WAITING);
        return waitlist.size() + 1;
    }

    private void promoteNextFromWaitlist(Integer lectureId) {
        lectureRepository.findById(lectureId).ifPresent(lecture -> {
            if (!hasAvailableSeat(lectureId, lecture.getCapacity())) {
                return;
            }
            enrollmentRepository.findFirstByLecture_IdAndStatusOrderByWaitlistPositionAsc(
                    lectureId, EnrollmentStatus.WAITING
            ).ifPresent(waiting -> {
                waiting.setStatus(EnrollmentStatus.ACTIVE);
                waiting.setWaitlistPosition(null);
                waiting.setApprovedAt(LocalDateTime.now());
                enrollmentRepository.save(waiting);
                reindexWaitlist(lectureId);
            });
        });
    }

    private void reindexWaitlist(Integer lectureId) {
        List<Enrollment> waitlist = enrollmentRepository
                .findAllByLecture_IdAndStatusOrderByWaitlistPositionAsc(lectureId, EnrollmentStatus.WAITING);
        int position = 1;
        for (Enrollment enrollment : waitlist) {
            enrollment.setWaitlistPosition(position++);
            enrollmentRepository.save(enrollment);
        }
    }
}
