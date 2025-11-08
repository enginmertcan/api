package com.mertcanengin.api.repository;

import com.mertcanengin.api.entity.EnrollmentGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IEnrollmentGradeRepository extends JpaRepository<EnrollmentGrade, Integer> {
    List<EnrollmentGrade> findAllByEnrollmentId(Integer enrollmentId);
    Optional<EnrollmentGrade> findByEnrollmentIdAndGradeComponentId(Integer enrollmentId, Integer componentId);
    void deleteByEnrollmentId(Integer enrollmentId);
}
