package com.mertcanengin.api.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.Classroom;
import com.mertcanengin.api.entity.Lecture;
import com.mertcanengin.api.entity.LectureSchedule;
import com.mertcanengin.api.entity.ScheduleSlot;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.repository.IClassroomRepository;
import com.mertcanengin.api.repository.ILectureRepository;
import com.mertcanengin.api.repository.ILectureScheduleRepository;
import com.mertcanengin.api.repository.IScheduleSlotRepository;

@ExtendWith(MockitoExtension.class)
class LectureScheduleServiceTest {

    @Mock
    private ILectureScheduleRepository lectureScheduleRepository;
    @Mock
    private ILectureRepository lectureRepository;
    @Mock
    private IClassroomRepository classroomRepository;
    @Mock
    private IScheduleSlotRepository scheduleSlotRepository;

    @InjectMocks
    private LectureScheduleService lectureScheduleService;

    private Lecture lecture;
    private Classroom classroom;
    private ScheduleSlot slot;

    @BeforeEach
    void setUp() {
        User teacher = new User();
        teacher.setId(5);

        lecture = new Lecture();
        lecture.setId(10);
        lecture.setName("Physics");
        lecture.setTeacher(teacher);

        classroom = new Classroom();
        classroom.setId(20);
        classroom.setName("A-101");
        classroom.setCapacity(30);

        slot = new ScheduleSlot();
        slot.setId(30);
        slot.setDayOfWeek(DayOfWeek.MONDAY);
        slot.setStartTime(LocalTime.of(9, 0));
        slot.setEndTime(LocalTime.of(11, 0));
    }

    @Test
    void scheduleFailsWhenLectureMissing() {
        LectureSchedule schedule = buildSchedule();
        Mockito.when(lectureRepository.findById(lecture.getId())).thenReturn(Optional.empty());

        Assertions.assertThrows(GeneralException.class, () -> lectureScheduleService.schedule(schedule));
    }

    @Test
    void scheduleFailsWhenClassroomConflict() {
        LectureSchedule schedule = buildSchedule();
        mockHappyPath();
        Mockito.when(lectureScheduleRepository.existsClassroomConflict(Mockito.eq(classroom.getId()),
                Mockito.eq(slot.getDayOfWeek()), Mockito.eq(slot.getStartTime()), Mockito.eq(slot.getEndTime()),
                Mockito.any(), Mockito.any(), Mockito.isNull()))
                .thenReturn(true);

        Assertions.assertThrows(GeneralException.class, () -> lectureScheduleService.schedule(schedule));
    }

    @Test
    void scheduleFailsWhenTeacherConflict() {
        LectureSchedule schedule = buildSchedule();
        mockHappyPath();
        Mockito.when(
                lectureScheduleRepository.existsClassroomConflict(
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any()
                )
        ).thenReturn(false);
        Mockito.when(
                lectureScheduleRepository.existsTeacherConflict(
                        Mockito.eq(lecture.getTeacher().getId()),
                        Mockito.eq(slot.getDayOfWeek()),
                        Mockito.eq(slot.getStartTime()),
                        Mockito.eq(slot.getEndTime()),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.isNull()
                )
        ).thenReturn(true);

        Assertions.assertThrows(GeneralException.class, () -> lectureScheduleService.schedule(schedule));
    }

    @Test
    void schedulePersistsWhenNoConflicts() {
        LectureSchedule schedule = buildSchedule();
        mockHappyPath();
        Mockito.when(
                lectureScheduleRepository.existsClassroomConflict(
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any()
                )
        ).thenReturn(false);
        Mockito.when(
                lectureScheduleRepository.existsTeacherConflict(
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any()
                )
        ).thenReturn(false);
        Mockito.when(lectureScheduleRepository.save(Mockito.any(LectureSchedule.class))).thenAnswer(invocation -> {
            LectureSchedule saved = invocation.getArgument(0);
            saved.setId(42);
            return saved;
        });

        LectureSchedule saved = lectureScheduleService.schedule(schedule);
        Assertions.assertEquals(42, saved.getId());
        Mockito.verify(lectureScheduleRepository).save(Mockito.any(LectureSchedule.class));
    }

    private LectureSchedule buildSchedule() {
        LectureSchedule schedule = new LectureSchedule();
        schedule.setLecture(lecture);
        schedule.setClassroom(classroom);
        schedule.setScheduleSlot(slot);
        schedule.setStartDate(LocalDate.now());
        schedule.setEndDate(LocalDate.now().plusMonths(1));
        return schedule;
    }

    private void mockHappyPath() {
        Mockito.when(lectureRepository.findById(lecture.getId())).thenReturn(Optional.of(lecture));
        Mockito.when(classroomRepository.findById(classroom.getId())).thenReturn(Optional.of(classroom));
        Mockito.when(scheduleSlotRepository.findById(slot.getId())).thenReturn(Optional.of(slot));
    }
}
