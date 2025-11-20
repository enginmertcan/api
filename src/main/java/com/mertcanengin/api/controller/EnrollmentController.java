package com.mertcanengin.api.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.mertcanengin.api.dto.*;
import com.mertcanengin.api.mapper.EnrollmentAttendanceMapper;
import com.mertcanengin.api.mapper.EnrollmentGradeMapper;
import com.mertcanengin.api.mapper.EnrollmentMapper;
import com.mertcanengin.api.service.IEnrollmentAttendanceService;
import com.mertcanengin.api.service.IEnrollmentGradeService;
import com.mertcanengin.api.service.IEnrollmentService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/enrollments")
@Tag(name = "Enrollments", description = "Öğrenci ders kayıt süreçleri")
public class EnrollmentController {

    private final IEnrollmentService enrollmentService;
    private final EnrollmentMapper enrollmentMapper;
    private final IEnrollmentGradeService enrollmentGradeService;
    private final EnrollmentGradeMapper enrollmentGradeMapper;
    private final IEnrollmentAttendanceService enrollmentAttendanceService;
    private final EnrollmentAttendanceMapper enrollmentAttendanceMapper;

    public EnrollmentController(IEnrollmentService enrollmentService,
                                EnrollmentMapper enrollmentMapper,
                                IEnrollmentGradeService enrollmentGradeService,
                                EnrollmentGradeMapper enrollmentGradeMapper,
                                IEnrollmentAttendanceService enrollmentAttendanceService,
                                EnrollmentAttendanceMapper enrollmentAttendanceMapper) {
        this.enrollmentService = enrollmentService;
        this.enrollmentMapper = enrollmentMapper;
        this.enrollmentGradeService = enrollmentGradeService;
        this.enrollmentGradeMapper = enrollmentGradeMapper;
        this.enrollmentAttendanceService = enrollmentAttendanceService;
        this.enrollmentAttendanceMapper = enrollmentAttendanceMapper;
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping
    ResponseEntity<EnrollmentResponse> enroll(@Valid @RequestBody EnrollmentRequest request) {
        return ResponseEntity.ok(
                enrollmentMapper.toResponse(
                        enrollmentService.enroll(request.lectureId(), request.studentId())
                )
        );
    }

    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    @PostMapping("/{id}/drop")
    ResponseEntity<EnrollmentResponse> drop(@PathVariable Integer id) {
        return ResponseEntity.ok(enrollmentMapper.toResponse(enrollmentService.drop(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/approve")
    ResponseEntity<EnrollmentResponse> approve(@PathVariable Integer id) {
        return ResponseEntity.ok(enrollmentMapper.toResponse(enrollmentService.approve(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/promote")
    ResponseEntity<EnrollmentResponse> promote(@PathVariable Integer id) {
        return ResponseEntity.ok(enrollmentMapper.toResponse(enrollmentService.promoteFromWaitlist(id)));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/{id}/complete")
    ResponseEntity<EnrollmentResponse> complete(@PathVariable Integer id,
                                                @Valid @RequestBody EnrollmentCompletionRequest request) {
        return ResponseEntity.ok(enrollmentMapper.toResponse(enrollmentService.complete(id, request.grade())));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/{id}/grades")
    ResponseEntity<EnrollmentGradeResponse> recordGrade(@PathVariable Integer id,
                                                        @Valid @RequestBody EnrollmentGradeRequest request) {
        return ResponseEntity.ok(
                enrollmentGradeMapper.toResponse(
                        enrollmentGradeService.recordGrade(id, request.gradeComponentId(), request.score())
                )
        );
    }

    @GetMapping("/{id}/grades")
    ResponseEntity<List<EnrollmentGradeResponse>> getGrades(@PathVariable Integer id) {
        return ResponseEntity.ok(
                enrollmentGradeMapper.toResponseList(enrollmentGradeService.getByEnrollment(id))
        );
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/{id}/attendance")
    ResponseEntity<EnrollmentAttendanceResponse> recordAttendance(@PathVariable Integer id,
                                                                  @Valid @RequestBody EnrollmentAttendanceRequest request) {
        return ResponseEntity.ok(
                enrollmentAttendanceMapper.toResponse(
                        enrollmentAttendanceService.recordAttendance(id, request.weekOf(), request.attended())
                )
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    @GetMapping("/{id}/attendance")
    ResponseEntity<List<EnrollmentAttendanceResponse>> getAttendance(@PathVariable Integer id) {
        return ResponseEntity.ok(
                enrollmentAttendanceMapper.toResponseList(enrollmentAttendanceService.getAttendance(id))
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    @GetMapping
    ResponseEntity<Page<EnrollmentResponse>> getEnrollments(@RequestParam(defaultValue = "0") Integer page,
                                                            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(
                enrollmentService.getAll(PageRequest.of(page, pageSize, Sort.by("id").descending()))
                        .map(enrollmentMapper::toResponse)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    @GetMapping("/lecture/{lectureId}")
    ResponseEntity<List<EnrollmentResponse>> getByLecture(@PathVariable Integer lectureId) {
        return ResponseEntity.ok(enrollmentMapper.toResponseList(enrollmentService.getByLecture(lectureId)));
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    @GetMapping("/student/{studentId}")
    ResponseEntity<List<EnrollmentResponse>> getByStudent(@PathVariable Integer studentId) {
        return ResponseEntity.ok(enrollmentMapper.toResponseList(enrollmentService.getByStudent(studentId)));
    }
}
