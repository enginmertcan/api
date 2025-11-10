package com.mertcanengin.api.repository;

import com.mertcanengin.api.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ILectureRepository extends JpaRepository<Lecture,Integer> {
    boolean existsByTeacher_Id(Integer teacherId);
    List<Lecture> findAllByTeacher_Id(Integer teacherId);
}
