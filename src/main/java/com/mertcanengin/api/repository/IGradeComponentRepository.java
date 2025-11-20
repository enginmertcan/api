package com.mertcanengin.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mertcanengin.api.entity.GradeComponent;

@Repository
public interface IGradeComponentRepository extends JpaRepository<GradeComponent, Integer> {
    List<GradeComponent> findAllByLectureId(Integer lectureId);
    boolean existsByLectureIdAndNameIgnoreCase(Integer lectureId, String name);
}
