package com.mertcanengin.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.domain.lecture.policy.LectureCreationPolicy;
import com.mertcanengin.api.entity.Lecture;
import com.mertcanengin.api.repository.ILectureRepository;
import com.mertcanengin.api.service.ILectureService;
import com.mertcanengin.api.service.MailService;

@Service
public class LectureService implements ILectureService {

    private final ILectureRepository lectureRepository;
    private final LectureCreationPolicy lectureCreationPolicy;
    private final MailService mailService;
    private final String academicOpsEmail;

    public LectureService(ILectureRepository lectureRepository,
                          LectureCreationPolicy lectureCreationPolicy,
                          MailService mailService,
                          @Value("${app.notifications.academic-ops-email:}") String academicOpsEmail) {
        this.lectureRepository = lectureRepository;
        this.lectureCreationPolicy = lectureCreationPolicy;
        this.mailService = mailService;
        this.academicOpsEmail = academicOpsEmail;
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Lecture save(Lecture lecture) {
        if (lecture == null) {
            throw new GeneralException("Lecture payload cannot be empty.");
        }
        Lecture preparedLecture = lectureCreationPolicy.prepareForSave(lecture);
        return lectureRepository.save(preparedLecture);
    }

    @Override
    public Lecture getById(Integer id) {
        return lectureRepository.findById(id)
                .orElseThrow(() -> new GeneralException("Lecture not found with id: " + id));
    }

    @Override
    public List<Lecture> getAll() {
        return lectureRepository.findAll();
    }

    @Override
    public Page<Lecture> getAll(Pageable pageable) {
        return lectureRepository.findAll(pageable);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Integer id) {
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new GeneralException("Lecture not found with id: " + id));
        lectureRepository.delete(lecture);
        notifyDeletion(lecture);
    }

    @Override
    public List<Lecture> getByTeacher(Integer teacherId) {
        return lectureRepository.findAllByTeacher_Id(teacherId);
    }

    private void notifyDeletion(Lecture lecture) {
        String subject = "Lecture Portal - Ders Silindi";
        String body = """
                %s dersinin kaydı %s tarafından silindi.
                Ders kimliği: %s
                Öğretim görevlisi: %s
                """.formatted(
                lecture.getName(),
                lecture.getUpdatedBy() != null ? lecture.getUpdatedBy() : "sistem",
                lecture.getId(),
                lecture.getTeacher() != null ? lecture.getTeacher().getFullName() : "Atanmamış"
        );
        if (lecture.getTeacher() != null && lecture.getTeacher().getEmail() != null) {
            mailService.sendActivityAlertEmail(lecture.getTeacher().getEmail(), subject, body);
        }
        if (academicOpsEmail != null && !academicOpsEmail.isBlank()) {
            mailService.sendActivityAlertEmail(academicOpsEmail, subject, body);
        }
    }
}
