package com.mertcanengin.api.repository;

import com.mertcanengin.api.entity.EnrollmentAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IEnrollmentAttendanceRepository extends JpaRepository<EnrollmentAttendance, Integer> {

    Optional<EnrollmentAttendance> findByEnrollment_IdAndWeekOf(Integer enrollmentId, LocalDate weekOf);

    List<EnrollmentAttendance> findAllByEnrollment_IdOrderByWeekOfAsc(Integer enrollmentId);

    long countByEnrollment_IdAndAttendedFalse(Integer enrollmentId);

    void deleteAllByEnrollment_Id(Integer enrollmentId);
}
