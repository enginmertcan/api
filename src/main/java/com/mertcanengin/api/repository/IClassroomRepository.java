package com.mertcanengin.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mertcanengin.api.entity.Classroom;

@Repository
public interface IClassroomRepository extends JpaRepository<Classroom, Integer> {
    boolean existsByNameIgnoreCase(String name);
}
