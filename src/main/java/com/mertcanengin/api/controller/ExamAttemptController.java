package com.mertcanengin.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.dto.exam.ExamAttemptResponse;
import com.mertcanengin.api.dto.exam.ExamAttemptStartResponse;
import com.mertcanengin.api.dto.exam.ExamAttemptSubmitRequest;
import com.mertcanengin.api.dto.exam.ExamPlayResponse;
import com.mertcanengin.api.entity.exam.ExamAttempt;
import com.mertcanengin.api.mapper.ExamMapper;
import com.mertcanengin.api.service.IExamAttemptService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/exams/{examId}/attempts")
@Tag(name = "Exam Attempts", description = "Öğrenci sınav etkileşimi")
public class ExamAttemptController {

    private final IExamAttemptService attemptService;
    private final ExamMapper examMapper;

    public ExamAttemptController(IExamAttemptService attemptService, ExamMapper examMapper) {
        this.attemptService = attemptService;
        this.examMapper = examMapper;
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping
    public ResponseEntity<ExamAttemptStartResponse> startAttempt(@PathVariable Integer examId) {
        ExamAttempt attempt = attemptService.startAttempt(examId);
        ExamPlayResponse playResponse = examMapper.toPlayResponse(attempt.getExam());
        return ResponseEntity.ok(examMapper.toAttemptStartResponse(attempt, playResponse));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/{attemptId}/submit")
    public ResponseEntity<ExamAttemptResponse> submitAttempt(@PathVariable Integer examId,
                                                             @PathVariable Integer attemptId,
                                                             @Valid @RequestBody ExamAttemptSubmitRequest request) {
        ExamAttempt attempt = attemptService.submitAttempt(attemptId, request);
        ensureExamMatches(examId, attempt);
        return ResponseEntity.ok(examMapper.toAttemptResponse(attempt));
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    @GetMapping("/{attemptId}")
    public ResponseEntity<ExamAttemptResponse> getAttempt(@PathVariable Integer examId,
                                                          @PathVariable Integer attemptId) {
        ExamAttempt attempt = attemptService.getAttempt(attemptId);
        ensureExamMatches(examId, attempt);
        return ResponseEntity.ok(examMapper.toAttemptResponse(attempt));
    }

    private void ensureExamMatches(Integer examId, ExamAttempt attempt) {
        if (attempt.getExam() == null || !attempt.getExam().getId().equals(examId)) {
            throw new GeneralException("İstenen oturum belirtilen sınava ait değil.");
        }
    }
}

