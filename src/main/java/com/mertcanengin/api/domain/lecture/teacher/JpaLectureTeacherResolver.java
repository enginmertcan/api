package com.mertcanengin.api.domain.lecture.teacher;

import org.springframework.stereotype.Component;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.entity.enums.Role;
import com.mertcanengin.api.repository.IUserRepository;

@Component
public class JpaLectureTeacherResolver implements LectureTeacherResolver {

    private final IUserRepository userRepository;

    public JpaLectureTeacherResolver(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User resolve(Integer teacherId) {
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
}
