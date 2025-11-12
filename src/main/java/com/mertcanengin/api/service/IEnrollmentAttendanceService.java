package com.mertcanengin.api.service;

import com.mertcanengin.api.entity.EnrollmentAttendance;

import java.time.LocalDate;
import java.util.List;

public interface IEnrollmentAttendanceService {

    EnrollmentAttendance recordAttendance(Integer enrollmentId, LocalDate weekOf, boolean attended);

    List<EnrollmentAttendance> getAttendance(Integer enrollmentId);
}
