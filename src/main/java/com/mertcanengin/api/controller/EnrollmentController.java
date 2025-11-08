package com.mertcanengin.api.controller;

import com.mertcanengin.api.dto.EnrollmentCompletionRequest;
import com.mertcanengin.api.dto.EnrollmentRequest;
import com.mertcanengin.api.dto.EnrollmentResponse;
import com.mertcanengin.api.mapper.EnrollmentMapper;
import com.mertcanengin.api.service.IEnrollmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final IEnrollmentService enrollmentService;
    private final EnrollmentMapper enrollmentMapper;

    public EnrollmentController(IEnrollmentService enrollmentService, EnrollmentMapper enrollmentMapper) {
        this.enrollmentService = enrollmentService;
        this.enrollmentMapper = enrollmentMapper;
    }

    @PostMapping
    ResponseEntity<EnrollmentResponse> enroll(@Valid @RequestBody EnrollmentRequest request) {
        return ResponseEntity.ok(
                enrollmentMapper.toResponse(
                        enrollmentService.enroll(request.lectureId(), request.studentId())
                )
        );
    }

    @PostMapping("/{id}/drop")
    ResponseEntity<EnrollmentResponse> drop(@PathVariable Integer id) {
        return ResponseEntity.ok(enrollmentMapper.toResponse(enrollmentService.drop(id)));
    }

    @PostMapping("/{id}/complete")
    ResponseEntity<EnrollmentResponse> complete(@PathVariable Integer id,
                                                @Valid @RequestBody EnrollmentCompletionRequest request) {
        return ResponseEntity.ok(enrollmentMapper.toResponse(enrollmentService.complete(id, request.grade())));
    }

    @GetMapping
    ResponseEntity<Page<EnrollmentResponse>> getEnrollments(@RequestParam(defaultValue = "0") Integer page,
                                                            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(
                enrollmentService.getAll(PageRequest.of(page, pageSize, Sort.by("id").descending()))
                        .map(enrollmentMapper::toResponse)
        );
    }

    @GetMapping("/lecture/{lectureId}")
    ResponseEntity<List<EnrollmentResponse>> getByLecture(@PathVariable Integer lectureId) {
        return ResponseEntity.ok(enrollmentMapper.toResponseList(enrollmentService.getByLecture(lectureId)));
    }

    @GetMapping("/student/{studentId}")
    ResponseEntity<List<EnrollmentResponse>> getByStudent(@PathVariable Integer studentId) {
        return ResponseEntity.ok(enrollmentMapper.toResponseList(enrollmentService.getByStudent(studentId)));
    }
}
