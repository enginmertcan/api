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
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    ResponseEntity<Page<LectureScheduleResponse>> getSchedules(@RequestParam(defaultValue = "0") Integer page,
                                                               @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(
                lectureScheduleService.getAll(PageRequest.of(page, pageSize, Sort.by("id").descending()))
                        .map(lectureScheduleMapper::toResponse)
        );
    }

    @GetMapping("/lecture/{lectureId}")
    ResponseEntity<List<LectureScheduleResponse>> getByLecture(@PathVariable Integer lectureId) {
        return ResponseEntity.ok(
                lectureScheduleMapper.toResponseList(lectureScheduleService.getByLecture(lectureId))
        );
    }

    @PostMapping
    ResponseEntity<LectureScheduleResponse> scheduleLecture(@Valid @RequestBody LectureScheduleRequest request) {
        return ResponseEntity.ok(
                lectureScheduleMapper.toResponse(
                        lectureScheduleService.schedule(lectureScheduleMapper.toEntity(request))
                )
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteSchedule(@PathVariable Integer id) {
        lectureScheduleService.delete(id);
        return ResponseEntity.ok().build();
    }
}
