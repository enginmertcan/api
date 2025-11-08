package com.mertcanengin.api.controller;

import com.mertcanengin.api.entity.Enrollment;
import com.mertcanengin.api.service.IEnrollmentService;
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

    public EnrollmentController(IEnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    ResponseEntity<Enrollment> enroll(@RequestBody EnrollmentRequest request) {
        return ResponseEntity.ok(enrollmentService.enroll(request.lectureId(), request.studentId()));
    }

    @PostMapping("/{id}/drop")
    ResponseEntity<Enrollment> drop(@PathVariable Integer id) {
        return ResponseEntity.ok(enrollmentService.drop(id));
    }

    @PostMapping("/{id}/complete")
    ResponseEntity<Enrollment> complete(@PathVariable Integer id, @RequestBody CompletionRequest request) {
        return ResponseEntity.ok(enrollmentService.complete(id, request.grade()));
    }

    @GetMapping
    ResponseEntity<Page<Enrollment>> getEnrollments(@RequestParam(defaultValue = "0") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(
                enrollmentService.getAll(PageRequest.of(page, pageSize, Sort.by("id").descending()))
        );
    }

    @GetMapping("/lecture/{lectureId}")
    ResponseEntity<List<Enrollment>> getByLecture(@PathVariable Integer lectureId) {
        return ResponseEntity.ok(enrollmentService.getByLecture(lectureId));
    }

    @GetMapping("/student/{studentId}")
    ResponseEntity<List<Enrollment>> getByStudent(@PathVariable Integer studentId) {
        return ResponseEntity.ok(enrollmentService.getByStudent(studentId));
    }

    private record EnrollmentRequest(Integer lectureId, Integer studentId) {
    }

    private record CompletionRequest(Double grade) {
    }
}
