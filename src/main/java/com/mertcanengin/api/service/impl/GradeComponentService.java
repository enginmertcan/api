package com.mertcanengin.api.service.impl;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.GradeComponent;
import com.mertcanengin.api.entity.Lecture;
import com.mertcanengin.api.repository.IGradeComponentRepository;
import com.mertcanengin.api.repository.ILectureRepository;
import com.mertcanengin.api.service.IGradeComponentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class GradeComponentService implements IGradeComponentService {

    private final IGradeComponentRepository gradeComponentRepository;
    private final ILectureRepository lectureRepository;

    public GradeComponentService(IGradeComponentRepository gradeComponentRepository,
                                 ILectureRepository lectureRepository) {
        this.gradeComponentRepository = gradeComponentRepository;
        this.lectureRepository = lectureRepository;
    }

    @Override
    public GradeComponent save(GradeComponent component) {
        Lecture lecture = resolveLecture(component);
        component.setLecture(lecture);

        validateRequest(component, lecture);
        return gradeComponentRepository.save(component);
    }

    private Lecture resolveLecture(GradeComponent component) {
        if (component.getLecture() == null || component.getLecture().getId() == null) {
            throw new GeneralException("Lecture id is required for grade component.");
        }
        return lectureRepository.findById(component.getLecture().getId())
                .orElseThrow(() -> new GeneralException("Lecture not found with id: " + component.getLecture().getId()));
    }

    @Override
    public GradeComponent getById(Integer id) {
        return gradeComponentRepository.findById(id)
                .orElseThrow(() -> new GeneralException("Grade component not found with id: " + id));
    }

    @Override
    public List<GradeComponent> getAll() {
        return gradeComponentRepository.findAll();
    }

    @Override
    public Page<GradeComponent> getAll(Pageable pageable) {
        return gradeComponentRepository.findAll(pageable);
    }

    @Override
    public void delete(Integer id) {
        GradeComponent component = getById(id);
        gradeComponentRepository.delete(component);
        validateWeights(component.getLecture().getId());
    }

    @Override
    public List<GradeComponent> getByLecture(Integer lectureId) {
        return gradeComponentRepository.findAllByLectureId(lectureId);
    }

    @Override
    public void validateWeights(Integer lectureId) {
        ensureTotalWeightValid(lectureId, null, null);
    }

    private void validateRequest(GradeComponent component, Lecture lecture) {
        if (component.getWeight() == null || component.getWeight().doubleValue() <= 0 || component.getWeight().doubleValue() > 100) {
            throw new GeneralException("Grade component weight must be between 0 and 100.");
        }
        if (component.getMaxScore() == null || component.getMaxScore().doubleValue() <= 0) {
            throw new GeneralException("Grade component max score must be greater than zero.");
        }
        component.setWeight(component.getWeight().setScale(2, RoundingMode.HALF_UP));
        component.setMaxScore(component.getMaxScore().setScale(2, RoundingMode.HALF_UP));
        if (component.getId() == null &&
                gradeComponentRepository.existsByLectureIdAndNameIgnoreCase(lecture.getId(), component.getName())) {
            throw new GeneralException("Grade component name already exists for this lecture.");
        }

        ensureTotalWeightValid(lecture.getId(), component.getId(), component.getWeight());
    }

    private void ensureTotalWeightValid(Integer lectureId, Integer componentId, BigDecimal newWeight) {
        List<GradeComponent> components = gradeComponentRepository.findAllByLectureId(lectureId);
        double total = components.stream()
                .mapToDouble(gc -> gc.getWeight().doubleValue())
                .sum();
        if (componentId != null) {
            total -= components.stream()
                    .filter(c -> c.getId().equals(componentId))
                    .findFirst()
                    .map(gc -> gc.getWeight().doubleValue())
                    .orElse(0.0);
        }
        if (newWeight != null) {
            total += newWeight.doubleValue();
        }
        if (total > 100.0 + 1e-6) {
            throw new GeneralException("Total weight of grade components cannot exceed 100.");
        }
    }
}
