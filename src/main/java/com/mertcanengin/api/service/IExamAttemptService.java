package com.mertcanengin.api.service;

import com.mertcanengin.api.dto.exam.ExamAttemptSubmitRequest;
import com.mertcanengin.api.entity.exam.ExamAttempt;

public interface IExamAttemptService {

    ExamAttempt startAttempt(Integer examId);

    ExamAttempt submitAttempt(Integer attemptId, ExamAttemptSubmitRequest request);

    ExamAttempt getAttempt(Integer attemptId);
}

