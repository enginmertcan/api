package com.mertcanengin.api.repository;

import com.mertcanengin.api.entity.LectureSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
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
            """)
    boolean existsClassroomConflict(@Param("classroomId") Integer classroomId,
                                    @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                    @Param("startTime") LocalTime startTime,
                                    @Param("endTime") LocalTime endTime,
                                    @Param("excludeId") Integer excludeId);

    @Query("""
            SELECT CASE WHEN COUNT(ls) > 0 THEN true ELSE false END
            FROM LectureSchedule ls
            WHERE ls.lecture.teacher.id = :teacherId
              AND (:excludeId IS NULL OR ls.id <> :excludeId)
              AND ls.scheduleSlot.dayOfWeek = :dayOfWeek
              AND ls.scheduleSlot.startTime < :endTime
              AND ls.scheduleSlot.endTime > :startTime
            """)
    boolean existsTeacherConflict(@Param("teacherId") Integer teacherId,
                                  @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                  @Param("startTime") LocalTime startTime,
                                  @Param("endTime") LocalTime endTime,
                                  @Param("excludeId") Integer excludeId);

    List<LectureSchedule> findAllByLectureId(Integer lectureId);
}
