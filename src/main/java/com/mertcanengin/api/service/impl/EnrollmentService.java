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
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new GeneralException("Lecture not found with id: " + lectureId));

        if (lecture.getCapacity() == null || lecture.getCapacity() <= 0) {
            throw new GeneralException("Lecture capacity must be greater than zero.");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new GeneralException("Student not found with id: " + studentId));

        if (student.getRole() != Role.STUDENT) {
            throw new GeneralException("User with id " + studentId + " is not registered as a student.");
        }

        Enrollment existing = enrollmentRepository.findByLecture_IdAndStudent_Id(lectureId, studentId).orElse(null);
        if (existing != null) {
            if (existing.getStatus() == EnrollmentStatus.ACTIVE) {
                throw new GeneralException("Student already enrolled in this lecture.");
            }
            if (existing.getStatus() == EnrollmentStatus.COMPLETED) {
                throw new GeneralException("Completed enrollment cannot be reactivated.");
            }
            return reactivateEnrollment(existing, lecture);
        }

        ensureCapacityAvailability(lectureId, lecture.getCapacity());

        Enrollment enrollment = new Enrollment();
        enrollment.setLecture(lecture);
        enrollment.setStudent(student);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setEnrolledAt(LocalDateTime.now());
        return enrollmentRepository.save(enrollment);
    }

    private Enrollment reactivateEnrollment(Enrollment enrollment, Lecture lecture) {
        ensureCapacityAvailability(lecture.getId(), lecture.getCapacity());
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setGrade(null);
        enrollment.setEnrolledAt(LocalDateTime.now());
        return enrollmentRepository.save(enrollment);
    }

    private void ensureCapacityAvailability(Integer lectureId, Integer capacity) {
        long activeCount = enrollmentRepository.countByLecture_IdAndStatus(lectureId, EnrollmentStatus.ACTIVE);
        if (activeCount >= capacity) {
            throw new GeneralException("Lecture capacity is full.");
        }
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
        enrollment.setStatus(EnrollmentStatus.DROPPED);
        return enrollmentRepository.save(enrollment);
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
        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        enrollment.setGrade(grade);
        return enrollmentRepository.save(enrollment);
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
        if (!enrollmentRepository.existsById(id)) {
            throw new GeneralException("Enrollment not found with id: " + id);
        }
        enrollmentRepository.deleteById(id);
    }
}
