package com.mertcanengin.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mertcanengin.api.entity.exam.ExamQuestionOption;

@Repository
public interface IExamQuestionOptionRepository extends JpaRepository<ExamQuestionOption, Integer> {
    Optional<ExamQuestionOption> findByIdAndQuestion_Id(Integer id, Integer questionId);
}

