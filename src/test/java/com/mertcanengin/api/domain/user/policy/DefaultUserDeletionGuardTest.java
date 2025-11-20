package com.mertcanengin.api.domain.user.policy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.repository.IEnrollmentRepository;
import com.mertcanengin.api.repository.ILectureRepository;

@ExtendWith(MockitoExtension.class)
class DefaultUserDeletionGuardTest {

    @Mock
    private ILectureRepository lectureRepository;
    @Mock
    private IEnrollmentRepository enrollmentRepository;

    @InjectMocks
    private DefaultUserDeletionGuard guard;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(10);
    }

    @Test
    void ensureCanDeleteThrowsWhenDependencies() {
        Mockito.when(lectureRepository.existsByTeacher_Id(user.getId())).thenReturn(true);
        Assertions.assertThrows(GeneralException.class, () -> guard.ensureCanDelete(user));
    }

    @Test
    void ensureCanDeletePassesWhenNoDependencies() {
        Assertions.assertDoesNotThrow(() -> guard.ensureCanDelete(user));
    }
}
