package com.mertcanengin.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mertcanengin.api.entity.exam.ExamQuestion;

@Repository
public interface IExamQuestionRepository extends JpaRepository<ExamQuestion, Integer> {
    Optional<ExamQuestion> findByIdAndExam_Id(Integer id, Integer examId);
}

