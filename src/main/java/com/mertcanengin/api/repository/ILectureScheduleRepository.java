package com.mertcanengin.api.repository;

import com.mertcanengin.api.entity.LectureSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ILectureScheduleRepository extends JpaRepository<LectureSchedule, Integer> {

    @Query("""
            SELECT CASE WHEN COUNT(ls) > 0 THEN true ELSE false END
            FROM LectureSchedule ls
            WHERE ls.classroom.id = :classroomId
              AND (:excludeId IS NULL OR ls.id <> :excludeId)
              AND ls.scheduleSlot.dayOfWeek = :dayOfWeek
              AND ls.scheduleSlot.startTime < :endTime
              AND ls.scheduleSlot.endTime > :startTime
              AND (
                    (ls.startDate IS NULL OR ls.startDate <= :endDate)
                AND (ls.endDate IS NULL OR ls.endDate >= :startDate)
                  )
            """)
    boolean existsClassroomConflict(@Param("classroomId") Integer classroomId,
                                    @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                    @Param("startTime") LocalTime startTime,
                                    @Param("endTime") LocalTime endTime,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate,
                                    @Param("excludeId") Integer excludeId);

    @Query("""
            SELECT CASE WHEN COUNT(ls) > 0 THEN true ELSE false END
            FROM LectureSchedule ls
            WHERE ls.lecture.teacher.id = :teacherId
              AND (:excludeId IS NULL OR ls.id <> :excludeId)
              AND ls.scheduleSlot.dayOfWeek = :dayOfWeek
              AND ls.scheduleSlot.startTime < :endTime
              AND ls.scheduleSlot.endTime > :startTime
              AND (
                    (ls.startDate IS NULL OR ls.startDate <= :endDate)
                AND (ls.endDate IS NULL OR ls.endDate >= :startDate)
                  )
            """)
    boolean existsTeacherConflict(@Param("teacherId") Integer teacherId,
                                  @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                  @Param("startTime") LocalTime startTime,
                                  @Param("endTime") LocalTime endTime,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate,
                                  @Param("excludeId") Integer excludeId);

    List<LectureSchedule> findAllByLectureId(Integer lectureId);

    @Query("""
            SELECT COUNT(ls)
            FROM LectureSchedule ls
            WHERE ls.endDate IS NULL OR ls.endDate >= :referenceDate
            """)
    long countActiveOrUpcomingSessions(@Param("referenceDate") LocalDate referenceDate);

    @Query("""
            SELECT COUNT(DISTINCT ls.classroom.id)
            FROM LectureSchedule ls
            WHERE ls.endDate IS NULL OR ls.endDate >= :referenceDate
            """)
    long countClassroomsInUse(@Param("referenceDate") LocalDate referenceDate);
}
