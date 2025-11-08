package com.mertcanengin.api.service;

import com.mertcanengin.api.entity.Enrollment;

import java.util.List;

public interface IEnrollmentService extends IService<Enrollment> {
    Enrollment enroll(Integer lectureId, Integer studentId);
    Enrollment approve(Integer enrollmentId);
    Enrollment promoteFromWaitlist(Integer enrollmentId);
    Enrollment drop(Integer enrollmentId);
    Enrollment complete(Integer enrollmentId, Double grade);
    List<Enrollment> getByLecture(Integer lectureId);
    List<Enrollment> getByStudent(Integer studentId);
}
