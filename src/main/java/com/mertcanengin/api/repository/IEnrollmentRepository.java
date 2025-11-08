package com.mertcanengin.api.repository;

import com.mertcanengin.api.entity.Enrollment;
import com.mertcanengin.api.entity.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IEnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    Optional<Enrollment> findByLecture_IdAndStudent_Id(Integer lectureId, Integer studentId);
    long countByLecture_IdAndStatus(Integer lectureId, EnrollmentStatus status);
    List<Enrollment> findAllByLecture_Id(Integer lectureId);
    List<Enrollment> findAllByStudent_Id(Integer studentId);
}
