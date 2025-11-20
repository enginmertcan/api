package com.mertcanengin.api.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.Classroom;
import com.mertcanengin.api.repository.IClassroomRepository;
import com.mertcanengin.api.service.IClassroomService;

@Service
public class ClassroomService implements IClassroomService {

    private final IClassroomRepository classroomRepository;

    public ClassroomService(IClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }

    @Override
    public Classroom save(Classroom classroom) {
        validate(classroom);
        return classroomRepository.save(classroom);
    }

    private void validate(Classroom classroom) {
        if (!StringUtils.hasText(classroom.getName())) {
            throw new GeneralException("Classroom name cannot be empty.");
        }
        if (classroom.getCapacity() == null || classroom.getCapacity() <= 0) {
            throw new GeneralException("Classroom capacity must be greater than zero.");
        }
        if (classroom.getId() == null && classroomRepository.existsByNameIgnoreCase(classroom.getName())) {
            throw new GeneralException("Classroom name already exists.");
        }
    }

    @Override
    public Classroom getById(Integer id) {
        return classroomRepository.findById(id)
                .orElseThrow(() -> new GeneralException("Classroom not found with id: " + id));
    }

    @Override
    public List<Classroom> getAll() {
        return classroomRepository.findAll();
    }

    @Override
    public Page<Classroom> getAll(Pageable pageable) {
        return classroomRepository.findAll(pageable);
    }

    @Override
    public void delete(Integer id) {
        if (!classroomRepository.existsById(id)) {
            throw new GeneralException("Classroom not found with id: " + id);
        }
        classroomRepository.deleteById(id);
    }
}
