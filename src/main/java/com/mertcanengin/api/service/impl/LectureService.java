package com.mertcanengin.api.service.impl;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.domain.lecture.policy.LectureCreationPolicy;
import com.mertcanengin.api.entity.Lecture;
import com.mertcanengin.api.repository.ILectureRepository;
import com.mertcanengin.api.service.ILectureService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LectureService implements ILectureService {

    private final ILectureRepository lectureRepository;
    private final LectureCreationPolicy lectureCreationPolicy;

    public LectureService(ILectureRepository lectureRepository,
                          LectureCreationPolicy lectureCreationPolicy) {
        this.lectureRepository = lectureRepository;
        this.lectureCreationPolicy = lectureCreationPolicy;
    }

    @Override
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
    public void delete(Integer id) {
        if (!lectureRepository.existsById(id)) {
            throw new GeneralException("Lecture not found with id: " + id);
        }
        lectureRepository.deleteById(id);
    }

    @Override
    public List<Lecture> getByTeacher(Integer teacherId) {
        return lectureRepository.findAllByTeacher_Id(teacherId);
    }
}
