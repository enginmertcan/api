package com.mertcanengin.api.domain.lecture.teacher;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.entity.enums.Role;
import com.mertcanengin.api.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

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
        assertThrows(GeneralException.class, () -> resolver.resolve(null));
    }

    @Test
    void resolveThrowsWhenNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(GeneralException.class, () -> resolver.resolve(user.getId()));
    }

    @Test
    void resolveThrowsWhenNotTeacher() {
        user.setRole(Role.STUDENT);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(GeneralException.class, () -> resolver.resolve(user.getId()));
    }

    @Test
    void resolveReturnsTeacher() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        User resolved = resolver.resolve(user.getId());
        assertEquals(user, resolved);
    }
}
