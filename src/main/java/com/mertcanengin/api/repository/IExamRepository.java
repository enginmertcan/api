package com.mertcanengin.api.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mertcanengin.api.entity.exam.Exam;
import com.mertcanengin.api.entity.enums.ExamStatus;

@Repository
public interface IExamRepository extends JpaRepository<Exam, Integer> {

    List<Exam> findAllByLecture_Id(Integer lectureId);

    List<Exam> findAllByLecture_IdAndStatusIn(Integer lectureId, List<ExamStatus> statuses);

    @EntityGraph(attributePaths = {"questions", "questions.options"})
    Optional<Exam> findWithQuestionsById(Integer id);

    List<Exam> findAllByLecture_IdAndOpensAtLessThanEqualAndClosesAtGreaterThan(Integer lectureId,
                                                                               LocalDateTime opensBefore,
                                                                               LocalDateTime closesAfter);
}

