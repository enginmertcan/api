package com.mertcanengin.api.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mertcanengin.api.dto.analytics.AnalyticsSummaryResponse;
import com.mertcanengin.api.dto.analytics.EnrollmentFunnelResponse;
import com.mertcanengin.api.dto.analytics.TeacherWorkloadResponse;
import com.mertcanengin.api.entity.Lecture;
import com.mertcanengin.api.entity.LectureSchedule;
import com.mertcanengin.api.entity.ScheduleSlot;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.entity.enums.EnrollmentStatus;
import com.mertcanengin.api.repository.IEnrollmentRepository;
import com.mertcanengin.api.repository.ILectureRepository;
import com.mertcanengin.api.repository.ILectureScheduleRepository;
import com.mertcanengin.api.service.AnalyticsService;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final ILectureRepository lectureRepository;
    private final ILectureScheduleRepository lectureScheduleRepository;
    private final IEnrollmentRepository enrollmentRepository;

    public AnalyticsServiceImpl(ILectureRepository lectureRepository,
                                ILectureScheduleRepository lectureScheduleRepository,
                                IEnrollmentRepository enrollmentRepository) {
        this.lectureRepository = lectureRepository;
        this.lectureScheduleRepository = lectureScheduleRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public AnalyticsSummaryResponse getSummary() {
        LocalDate today = LocalDate.now();
        long totalLectures = lectureRepository.count();
        long activeEnrollments = enrollmentRepository.countByStatus(EnrollmentStatus.ACTIVE);
        long waitlisted = enrollmentRepository.countByStatus(EnrollmentStatus.WAITING);
        long classroomsInUse = lectureScheduleRepository.countClassroomsInUse(today);
        long upcomingSessions = lectureScheduleRepository.countActiveOrUpcomingSessions(today);
        return new AnalyticsSummaryResponse(totalLectures, activeEnrollments, waitlisted, classroomsInUse, upcomingSessions);
    }

    @Override
    public List<TeacherWorkloadResponse> getTeacherWorkload() {
        List<Lecture> lectures = lectureRepository.findAll();
        List<LectureSchedule> schedules = lectureScheduleRepository.findAll();

        Map<Integer, TeacherAggregate> aggregates = new HashMap<>();

        for (Lecture lecture : lectures) {
            User teacher = lecture.getTeacher();
            if (teacher == null) {
                continue;
            }
            TeacherAggregate aggregate = aggregates.computeIfAbsent(
                    teacher.getId(),
                    id -> new TeacherAggregate(teacher.getId(), teacher.getName() + " " + teacher.getSurname())
            );
            aggregate.lectureIds.add(lecture.getId());
        }

        for (LectureSchedule schedule : schedules) {
            Lecture lecture = schedule.getLecture();
            if (lecture == null || lecture.getTeacher() == null) {
                continue;
            }
            TeacherAggregate aggregate = aggregates.computeIfAbsent(
                    lecture.getTeacher().getId(),
                    id -> new TeacherAggregate(lecture.getTeacher().getId(),
                            lecture.getTeacher().getName() + " " + lecture.getTeacher().getSurname())
            );
            ScheduleSlot slot = schedule.getScheduleSlot();
            if (slot != null && slot.getStartTime() != null && slot.getEndTime() != null) {
                long minutes = Duration.between(slot.getStartTime(), slot.getEndTime()).toMinutes();
                if (minutes > 0) {
                    aggregate.weeklyMinutes += minutes;
                }
            }
        }

        return aggregates.values().stream()
                .map(agg -> new TeacherWorkloadResponse(
                        agg.teacherId,
                        agg.teacherName,
                        agg.lectureIds.size(),
                        agg.weeklyMinutes / 60.0
                ))
                .sorted((a, b) -> Double.compare(b.weeklyHours(), a.weeklyHours()))
                .collect(Collectors.toList());
    }

    @Override
    public EnrollmentFunnelResponse getEnrollmentFunnel() {
        Map<String, Long> counts = new HashMap<>();
        List<IEnrollmentRepository.EnrollmentStatusCountProjection> aggregates = enrollmentRepository.aggregateStatusCounts();
        for (IEnrollmentRepository.EnrollmentStatusCountProjection projection : aggregates) {
            counts.put(projection.getStatus().name(), projection.getTotal());
        }
        for (EnrollmentStatus status : EnrollmentStatus.values()) {
            counts.putIfAbsent(status.name(), 0L);
        }
        return new EnrollmentFunnelResponse(counts);
    }

    private static class TeacherAggregate {
        private final Integer teacherId;
        private final String teacherName;
        private final Set<Integer> lectureIds = new HashSet<>();
        private long weeklyMinutes = 0L;

        private TeacherAggregate(Integer teacherId, String teacherName) {
            this.teacherId = teacherId;
            this.teacherName = teacherName;
        }
    }
}
