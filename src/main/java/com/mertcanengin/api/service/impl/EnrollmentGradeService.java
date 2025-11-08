package com.mertcanengin.api.service.impl;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.Enrollment;
import com.mertcanengin.api.entity.EnrollmentGrade;
import com.mertcanengin.api.entity.GradeComponent;
import com.mertcanengin.api.entity.enums.EnrollmentStatus;
import com.mertcanengin.api.repository.IEnrollmentGradeRepository;
import com.mertcanengin.api.repository.IEnrollmentRepository;
import com.mertcanengin.api.repository.IGradeComponentRepository;
import com.mertcanengin.api.service.IEnrollmentGradeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnrollmentGradeService implements IEnrollmentGradeService {

    private final IEnrollmentGradeRepository enrollmentGradeRepository;
    private final IEnrollmentRepository enrollmentRepository;
    private final IGradeComponentRepository gradeComponentRepository;

    public EnrollmentGradeService(IEnrollmentGradeRepository enrollmentGradeRepository,
                                  IEnrollmentRepository enrollmentRepository,
                                  IGradeComponentRepository gradeComponentRepository) {
        this.enrollmentGradeRepository = enrollmentGradeRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.gradeComponentRepository = gradeComponentRepository;
    }

    @Override
    @Transactional
    public EnrollmentGrade recordGrade(Integer enrollmentId, Integer componentId, Double score) {
        Enrollment enrollment = getEnrollment(enrollmentId);
        if (enrollment.getStatus() == EnrollmentStatus.DROPPED) {
            throw new GeneralException("Cannot record grade for a dropped enrollment.");
        }

        GradeComponent component = gradeComponentRepository.findById(componentId)
                .orElseThrow(() -> new GeneralException("Grade component not found with id: " + componentId));

        if (!component.getLecture().getId().equals(enrollment.getLecture().getId())) {
            throw new GeneralException("Grade component does not belong to the same lecture.");
        }

        if (score < 0 || score > component.getMaxScore()) {
            throw new GeneralException("Score must be between 0 and " + component.getMaxScore());
        }

        EnrollmentGrade grade = enrollmentGradeRepository
                .findByEnrollmentIdAndGradeComponentId(enrollmentId, componentId)
                .orElse(new EnrollmentGrade());
        grade.setEnrollment(enrollment);
        grade.setGradeComponent(component);
        grade.setScore(score);

        EnrollmentGrade saved = enrollmentGradeRepository.save(grade);
        recalculateFinalGrade(enrollment);
        return saved;
    }

    private void recalculateFinalGrade(Enrollment enrollment) {
        List<GradeComponent> components = gradeComponentRepository.findAllByLectureId(enrollment.getLecture().getId());
        if (components.isEmpty()) {
            return;
        }
        List<EnrollmentGrade> grades = enrollmentGradeRepository.findAllByEnrollmentId(enrollment.getId());

        double totalWeight = 0;
        double totalScore = 0;

        for (GradeComponent component : components) {
            totalWeight += component.getWeight();
            EnrollmentGrade enrollmentGrade = grades.stream()
                    .filter(g -> g.getGradeComponent().getId().equals(component.getId()))
                    .findFirst()
                    .orElse(null);
            if (enrollmentGrade != null) {
                double normalized = enrollmentGrade.getScore() / component.getMaxScore();
                totalScore += normalized * component.getWeight();
            }
        }

        if (totalWeight == 0) {
            return;
        }

        double finalGrade = (totalScore / totalWeight) * 100.0;
        enrollment.setFinalGrade(Math.round(finalGrade * 100.0) / 100.0);
        enrollment.setPassed(enrollment.getFinalGrade() != null && enrollment.getFinalGrade() >= 60.0);
        enrollmentRepository.save(enrollment);
    }

    private Enrollment getEnrollment(Integer enrollmentId) {
        return enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new GeneralException("Enrollment not found with id: " + enrollmentId));
    }

    @Override
    public EnrollmentGrade save(EnrollmentGrade enrollmentGrade) {
        throw new UnsupportedOperationException("Use recordGrade instead of save.");
    }

    @Override
    public EnrollmentGrade getById(Integer id) {
        return enrollmentGradeRepository.findById(id)
                .orElseThrow(() -> new GeneralException("Enrollment grade not found with id: " + id));
    }

    @Override
    public List<EnrollmentGrade> getAll() {
        return enrollmentGradeRepository.findAll();
    }

    @Override
    public Page<EnrollmentGrade> getAll(Pageable pageable) {
        return enrollmentGradeRepository.findAll(pageable);
    }

    @Override
    public void delete(Integer id) {
        enrollmentGradeRepository.deleteById(id);
    }

    @Override
    public List<EnrollmentGrade> getByEnrollment(Integer enrollmentId) {
        return enrollmentGradeRepository.findAllByEnrollmentId(enrollmentId);
    }
}
