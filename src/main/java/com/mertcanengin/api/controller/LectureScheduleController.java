package com.mertcanengin.api.controller;

import com.mertcanengin.api.dto.LectureScheduleRequest;
import com.mertcanengin.api.dto.LectureScheduleResponse;
import com.mertcanengin.api.entity.Lecture;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.entity.enums.Role;
import com.mertcanengin.api.mapper.LectureScheduleMapper;
import com.mertcanengin.api.security.UserPrincipal;
import com.mertcanengin.api.service.IEnrollmentService;
import com.mertcanengin.api.service.ILectureScheduleService;
import com.mertcanengin.api.service.ILectureService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lecture-schedules")
public class LectureScheduleController {

    private final ILectureScheduleService lectureScheduleService;
    private final LectureScheduleMapper lectureScheduleMapper;
    private final IEnrollmentService enrollmentService;
    private final ILectureService lectureService;

    public LectureScheduleController(ILectureScheduleService lectureScheduleService,
                                     LectureScheduleMapper lectureScheduleMapper,
                                     IEnrollmentService enrollmentService,
                                     ILectureService lectureService) {
        this.lectureScheduleService = lectureScheduleService;
        this.lectureScheduleMapper = lectureScheduleMapper;
        this.enrollmentService = enrollmentService;
        this.lectureService = lectureService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    @GetMapping
    ResponseEntity<Page<LectureScheduleResponse>> getSchedules(@RequestParam(defaultValue = "0") Integer page,
                                                               @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(
                lectureScheduleService.getAll(PageRequest.of(page, pageSize, Sort.by("id").descending()))
                        .map(lectureScheduleMapper::toResponse)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    @GetMapping("/lecture/{lectureId}")
    ResponseEntity<List<LectureScheduleResponse>> getByLecture(@PathVariable Integer lectureId) {
        return ResponseEntity.ok(
                lectureScheduleMapper.toResponseList(lectureScheduleService.getByLecture(lectureId))
        );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my")
    ResponseEntity<List<LectureScheduleResponse>> getMySchedules(@AuthenticationPrincipal UserPrincipal principal) {
        List<Integer> lectureIds = resolveLectureIds(principal.getUser());
        if (lectureIds.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(
                lectureScheduleMapper.toResponseList(lectureScheduleService.getByLectures(lectureIds))
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @PostMapping
    ResponseEntity<LectureScheduleResponse> scheduleLecture(@Valid @RequestBody LectureScheduleRequest request) {
        return ResponseEntity.ok(
                lectureScheduleMapper.toResponse(
                        lectureScheduleService.schedule(lectureScheduleMapper.toEntity(request))
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteSchedule(@PathVariable Integer id) {
        lectureScheduleService.delete(id);
        return ResponseEntity.ok().build();
    }

    private List<Integer> resolveLectureIds(User user) {
        Role role = user.getRole();
        if (role == Role.STUDENT) {
            return enrollmentService.getByStudent(user.getId()).stream()
                    .map(enrollment -> enrollment.getLecture() != null ? enrollment.getLecture().getId() : null)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
        }
        if (role == Role.TEACHER) {
            return lectureService.getByTeacher(user.getId()).stream()
                    .map(Lecture::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
