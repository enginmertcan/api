package com.mertcanengin.api.domain.lecture.teacher;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.entity.enums.Role;
import com.mertcanengin.api.repository.IUserRepository;

@ExtendWith(MockitoExtension.class)
class JpaLectureTeacherResolverTest {

    @Mock
    private IUserRepository userRepository;

    private JpaLectureTeacherResolver resolver;
    private User user;

    @BeforeEach
    void setUp() {
        resolver = new JpaLectureTeacherResolver(userRepository);
        user = new User();
        user.setId(3);
        user.setRole(Role.TEACHER);
    }

    @Test
    void resolveThrowsWhenIdMissing() {
        Assertions.assertThrows(GeneralException.class, () -> resolver.resolve(null));
    }

    @Test
    void resolveThrowsWhenNotFound() {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(GeneralException.class, () -> resolver.resolve(user.getId()));
    }

    @Test
    void resolveThrowsWhenNotTeacher() {
        user.setRole(Role.STUDENT);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Assertions.assertThrows(GeneralException.class, () -> resolver.resolve(user.getId()));
    }

    @Test
    void resolveReturnsTeacher() {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        User resolved = resolver.resolve(user.getId());
        Assertions.assertEquals(user, resolved);
    }
}
