package com.mertcanengin.api.service;

import java.util.List;

import com.mertcanengin.api.dto.exam.ExamRequest;
import com.mertcanengin.api.entity.exam.Exam;

public interface IExamService {

    Exam createExam(ExamRequest request);

    Exam updateExam(Integer examId, ExamRequest request);

    Exam getExam(Integer examId);

    Exam getExamWithQuestions(Integer examId);

    List<Exam> getLectureExams(Integer lectureId);

    List<Exam> getAvailableExams(Integer lectureId);
}

