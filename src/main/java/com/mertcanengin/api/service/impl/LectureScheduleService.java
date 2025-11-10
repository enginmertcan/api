package com.mertcanengin.api.service.impl;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.Classroom;
import com.mertcanengin.api.entity.Lecture;
import com.mertcanengin.api.entity.LectureSchedule;
import com.mertcanengin.api.entity.ScheduleSlot;
import com.mertcanengin.api.repository.IClassroomRepository;
import com.mertcanengin.api.repository.ILectureRepository;
import com.mertcanengin.api.repository.ILectureScheduleRepository;
import com.mertcanengin.api.repository.IScheduleSlotRepository;
import com.mertcanengin.api.service.ILectureScheduleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Service
public class LectureScheduleService implements ILectureScheduleService {

    private static final LocalDate OPEN_START = LocalDate.of(1900, Month.JANUARY, 1);
    private static final LocalDate OPEN_END = LocalDate.of(2100, Month.DECEMBER, 31);

    private final ILectureScheduleRepository lectureScheduleRepository;
    private final ILectureRepository lectureRepository;
    private final IClassroomRepository classroomRepository;
    private final IScheduleSlotRepository scheduleSlotRepository;

    public LectureScheduleService(ILectureScheduleRepository lectureScheduleRepository,
                                  ILectureRepository lectureRepository,
                                  IClassroomRepository classroomRepository,
                                  IScheduleSlotRepository scheduleSlotRepository) {
        this.lectureScheduleRepository = lectureScheduleRepository;
        this.lectureRepository = lectureRepository;
        this.classroomRepository = classroomRepository;
        this.scheduleSlotRepository = scheduleSlotRepository;
    }

    @Override
    public LectureSchedule schedule(LectureSchedule lectureSchedule) {
        return save(lectureSchedule);
    }

    @Override
    public LectureSchedule save(LectureSchedule lectureSchedule) {
        Lecture lecture = resolveLecture(lectureSchedule);
        Classroom classroom = resolveClassroom(lectureSchedule);
        ScheduleSlot scheduleSlot = resolveSlot(lectureSchedule);
        LocalDate startDate = lectureSchedule.getStartDate();
        LocalDate endDate = lectureSchedule.getEndDate();
        validateDateRange(startDate, endDate);

        Integer excludeId = lectureSchedule.getId();
        ensureNoConflicts(classroom.getId(), lecture.getTeacher().getId(), scheduleSlot, startDate, endDate, excludeId);

        lectureSchedule.setLecture(lecture);
        lectureSchedule.setClassroom(classroom);
        lectureSchedule.setScheduleSlot(scheduleSlot);
        return lectureScheduleRepository.save(lectureSchedule);
    }

    private Lecture resolveLecture(LectureSchedule lectureSchedule) {
        Integer lectureId = lectureSchedule.getLecture() != null ? lectureSchedule.getLecture().getId() : null;
        if (lectureId == null) {
            throw new GeneralException("Lecture id is required for scheduling.");
        }
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new GeneralException("Lecture not found with id: " + lectureId));
        if (lecture.getTeacher() == null) {
            throw new GeneralException("Lecture must have an assigned teacher.");
        }
        return lecture;
    }

    private Classroom resolveClassroom(LectureSchedule lectureSchedule) {
        Integer classroomId = lectureSchedule.getClassroom() != null ? lectureSchedule.getClassroom().getId() : null;
        if (classroomId == null) {
            throw new GeneralException("Classroom id is required for scheduling.");
        }
        return classroomRepository.findById(classroomId)
                .orElseThrow(() -> new GeneralException("Classroom not found with id: " + classroomId));
    }

    private ScheduleSlot resolveSlot(LectureSchedule lectureSchedule) {
        Integer slotId = lectureSchedule.getScheduleSlot() != null ? lectureSchedule.getScheduleSlot().getId() : null;
        if (slotId == null) {
            throw new GeneralException("Schedule slot id is required for scheduling.");
        }
        return scheduleSlotRepository.findById(slotId)
                .orElseThrow(() -> new GeneralException("Schedule slot not found with id: " + slotId));
    }

    private void validateDateRange(LocalDate start, LocalDate end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new GeneralException("Schedule start date cannot be after end date.");
        }
    }

    private void ensureNoConflicts(Integer classroomId,
                                   Integer teacherId,
                                   ScheduleSlot slot,
                                   LocalDate startDate,
                                   LocalDate endDate,
                                   Integer excludeId) {
        LocalDate normalizedStart = startDate != null ? startDate : OPEN_START;
        LocalDate normalizedEnd = endDate != null ? endDate : OPEN_END;

        boolean classroomConflict = lectureScheduleRepository.existsClassroomConflict(
                classroomId,
                slot.getDayOfWeek(),
                slot.getStartTime(),
                slot.getEndTime(),
                normalizedStart,
                normalizedEnd,
                excludeId);
        if (classroomConflict) {
            throw new GeneralException("Classroom already has a lecture at this time.");
        }

        boolean teacherConflict = lectureScheduleRepository.existsTeacherConflict(
                teacherId,
                slot.getDayOfWeek(),
                slot.getStartTime(),
                slot.getEndTime(),
                normalizedStart,
                normalizedEnd,
                excludeId);
        if (teacherConflict) {
            throw new GeneralException("Teacher already has a lecture at this time.");
        }
    }

    @Override
    public LectureSchedule getById(Integer id) {
        return lectureScheduleRepository.findById(id)
                .orElseThrow(() -> new GeneralException("Lecture schedule not found with id: " + id));
    }

    @Override
    public List<LectureSchedule> getAll() {
        return lectureScheduleRepository.findAll();
    }

    @Override
    public Page<LectureSchedule> getAll(Pageable pageable) {
        return lectureScheduleRepository.findAll(pageable);
    }

    @Override
    public void delete(Integer id) {
        if (!lectureScheduleRepository.existsById(id)) {
            throw new GeneralException("Lecture schedule not found with id: " + id);
        }
        lectureScheduleRepository.deleteById(id);
    }

    @Override
    public List<LectureSchedule> getByLecture(Integer lectureId) {
        return lectureScheduleRepository.findAllByLectureId(lectureId);
    }

    @Override
    public List<LectureSchedule> getByLectures(List<Integer> lectureIds) {
        if (lectureIds == null || lectureIds.isEmpty()) {
            return List.of();
        }
        return lectureScheduleRepository.findAllByLecture_IdIn(lectureIds);
    }
}
