package com.mertcanengin.api.domain.user.policy;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.repository.IEnrollmentRepository;
import com.mertcanengin.api.repository.ILectureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

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
        when(lectureRepository.existsByTeacher_Id(user.getId())).thenReturn(true);
        assertThrows(GeneralException.class, () -> guard.ensureCanDelete(user));
    }

    @Test
    void ensureCanDeletePassesWhenNoDependencies() {
        assertDoesNotThrow(() -> guard.ensureCanDelete(user));
    }
}
