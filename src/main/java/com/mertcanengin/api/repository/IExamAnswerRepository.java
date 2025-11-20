package com.mertcanengin.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mertcanengin.api.entity.exam.ExamAnswer;

@Repository
public interface IExamAnswerRepository extends JpaRepository<ExamAnswer, Integer> {
    void deleteAllByAttempt_Id(Integer attemptId);
}

