package com.mertcanengin.api.service;

import java.util.List;

import com.mertcanengin.api.entity.EnrollmentGrade;

public interface IEnrollmentGradeService extends IService<EnrollmentGrade> {
    List<EnrollmentGrade> getByEnrollment(Integer enrollmentId);
    EnrollmentGrade recordGrade(Integer enrollmentId, Integer componentId, Double score);
}
