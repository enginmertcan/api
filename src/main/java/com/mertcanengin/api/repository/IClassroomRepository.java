package com.mertcanengin.api.repository;

import com.mertcanengin.api.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IClassroomRepository extends JpaRepository<Classroom, Integer> {
    boolean existsByNameIgnoreCase(String name);
}
