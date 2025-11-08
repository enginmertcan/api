package com.mertcanengin.api.repository;

import com.mertcanengin.api.entity.GradeComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IGradeComponentRepository extends JpaRepository<GradeComponent, Integer> {
    List<GradeComponent> findAllByLectureId(Integer lectureId);
    boolean existsByLectureIdAndNameIgnoreCase(Integer lectureId, String name);
}
