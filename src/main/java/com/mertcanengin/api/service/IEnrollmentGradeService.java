package com.mertcanengin.api.service;

import com.mertcanengin.api.entity.EnrollmentGrade;

import java.util.List;

public interface IEnrollmentGradeService extends IService<EnrollmentGrade> {
    List<EnrollmentGrade> getByEnrollment(Integer enrollmentId);
    EnrollmentGrade recordGrade(Integer enrollmentId, Integer componentId, Double score);
}
