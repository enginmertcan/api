package com.mertcanengin.api.repository;

import com.mertcanengin.api.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILectureRepository extends JpaRepository<Lecture,Integer> {


}
