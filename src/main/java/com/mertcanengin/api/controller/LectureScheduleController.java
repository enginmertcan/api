package com.mertcanengin.api.controller;

import com.mertcanengin.api.dto.LectureScheduleRequest;
import com.mertcanengin.api.dto.LectureScheduleResponse;
import com.mertcanengin.api.mapper.LectureScheduleMapper;
import com.mertcanengin.api.service.ILectureScheduleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lecture-schedules")
public class LectureScheduleController {

    private final ILectureScheduleService lectureScheduleService;
    private final LectureScheduleMapper lectureScheduleMapper;

    public LectureScheduleController(ILectureScheduleService lectureScheduleService,
                                     LectureScheduleMapper lectureScheduleMapper) {
        this.lectureScheduleService = lectureScheduleService;
        this.lectureScheduleMapper = lectureScheduleMapper;
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
}
