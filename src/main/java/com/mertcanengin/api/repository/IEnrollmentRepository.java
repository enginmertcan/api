package com.mertcanengin.api.repository;

import com.mertcanengin.api.entity.Enrollment;
import com.mertcanengin.api.entity.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface IEnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    Optional<Enrollment> findByLecture_IdAndStudent_Id(Integer lectureId, Integer studentId);
    long countByLecture_IdAndStatus(Integer lectureId, EnrollmentStatus status);
    long countByLecture_IdAndStatusIn(Integer lectureId, Collection<EnrollmentStatus> statuses);
    long countByStatus(EnrollmentStatus status);
    long countByStatusIn(Collection<EnrollmentStatus> statuses);

    @Query("""
            SELECT e.status AS status, COUNT(e) AS total
            FROM Enrollment e
            GROUP BY e.status
            """)
    List<EnrollmentStatusCountProjection> aggregateStatusCounts();

    List<Enrollment> findAllByLecture_Id(Integer lectureId);
    List<Enrollment> findAllByLecture_IdAndStatusOrderByWaitlistPositionAsc(Integer lectureId, EnrollmentStatus status);
    Optional<Enrollment> findFirstByLecture_IdAndStatusOrderByWaitlistPositionAsc(Integer lectureId, EnrollmentStatus status);
    List<Enrollment> findAllByStudent_Id(Integer studentId);
    boolean existsByStudent_Id(Integer studentId);

    interface EnrollmentStatusCountProjection {
        EnrollmentStatus getStatus();
        long getTotal();
    }
}
