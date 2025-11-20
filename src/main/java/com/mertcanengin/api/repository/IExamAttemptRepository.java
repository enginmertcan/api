package com.mertcanengin.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mertcanengin.api.entity.exam.ExamAttempt;

@Repository
public interface IExamAttemptRepository extends JpaRepository<ExamAttempt, Integer> {

    Optional<ExamAttempt> findByExam_IdAndStudent_Id(Integer examId, Integer studentId);

    @EntityGraph(attributePaths = {
            "exam",
            "exam.questions",
            "exam.questions.options",
            "answers",
            "answers.question",
            "answers.selectedOption"
    })
    Optional<ExamAttempt> findDetailedById(Integer id);
}

