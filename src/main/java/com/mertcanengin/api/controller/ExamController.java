package com.mertcanengin.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mertcanengin.api.dto.exam.ExamRequest;
import com.mertcanengin.api.dto.exam.ExamResponse;
import com.mertcanengin.api.dto.exam.ExamSummaryResponse;
import com.mertcanengin.api.mapper.ExamMapper;
import com.mertcanengin.api.service.IExamService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/exams")
@Tag(name = "Exams", description = "Sınav yönetimi")
public class ExamController {

    private final IExamService examService;
    private final ExamMapper examMapper;

    public ExamController(IExamService examService, ExamMapper examMapper) {
        this.examService = examService;
        this.examMapper = examMapper;
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @PostMapping
    public ResponseEntity<ExamResponse> createExam(@Valid @RequestBody ExamRequest request) {
        return ResponseEntity.ok(examMapper.toResponse(examService.createExam(request)));
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @PutMapping("/{examId}")
    public ResponseEntity<ExamResponse> updateExam(@PathVariable Integer examId,
                                                   @Valid @RequestBody ExamRequest request) {
        return ResponseEntity.ok(examMapper.toResponse(examService.updateExam(examId, request)));
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping("/{examId}")
    public ResponseEntity<ExamResponse> getExam(@PathVariable Integer examId) {
        return ResponseEntity.ok(examMapper.toResponse(examService.getExamWithQuestions(examId)));
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping("/lecture/{lectureId}")
    public ResponseEntity<List<ExamSummaryResponse>> listLectureExams(@PathVariable Integer lectureId) {
        return ResponseEntity.ok(
                examService.getLectureExams(lectureId).stream()
                        .map(examMapper::toSummary)
                        .toList()
        );
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/lecture/{lectureId}/available")
    public ResponseEntity<List<ExamSummaryResponse>> listAvailableExams(@PathVariable Integer lectureId) {
        return ResponseEntity.ok(
                examService.getAvailableExams(lectureId).stream()
                        .map(examMapper::toSummary)
                        .toList()
        );
    }
}

