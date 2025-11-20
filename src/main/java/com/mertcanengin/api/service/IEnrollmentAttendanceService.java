package com.mertcanengin.api.service;

import java.time.LocalDate;
import java.util.List;

import com.mertcanengin.api.entity.EnrollmentAttendance;

public interface IEnrollmentAttendanceService {

    EnrollmentAttendance recordAttendance(Integer enrollmentId, LocalDate weekOf, boolean attended);

    List<EnrollmentAttendance> getAttendance(Integer enrollmentId);
}
