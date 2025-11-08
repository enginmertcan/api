package com.mertcanengin.api.service.impl;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.Lecture;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.entity.enums.Role;
import com.mertcanengin.api.repository.ILectureRepository;
import com.mertcanengin.api.repository.IUserRepository;
import com.mertcanengin.api.service.ILectureService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class LectureService implements ILectureService {

    private final ILectureRepository lectureRepository;
    private final IUserRepository userRepository;

    public LectureService(ILectureRepository lectureRepository, IUserRepository userRepository) {
        this.lectureRepository = lectureRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Lecture save(Lecture lecture) {
        if (!StringUtils.hasText(lecture.getName())) {
            throw new GeneralException("Lecture name cannot be empty.");
        }
        if (lecture.getCapacity() == null || lecture.getCapacity() <= 0) {
            throw new GeneralException("Lecture capacity must be greater than zero.");
        }
        User teacher = resolveTeacher(lecture);
        lecture.setTeacher(teacher);
        return lectureRepository.save(lecture);
    }

    private User resolveTeacher(Lecture lecture) {
        Integer teacherId = lecture.getTeacherId();
        if (teacherId == null) {
            throw new GeneralException("A lecture must be linked to a teacher.");
        }
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new GeneralException("Teacher not found with id: " + teacherId));
        if (teacher.getRole() != Role.TEACHER) {
            throw new GeneralException("User with id " + teacherId + " is not a teacher.");
        }
        return teacher;
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
}
