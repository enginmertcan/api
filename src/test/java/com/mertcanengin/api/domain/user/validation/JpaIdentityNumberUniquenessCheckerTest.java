package com.mertcanengin.api.domain.user.validation;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.repository.IUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaIdentityNumberUniquenessCheckerTest {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private JpaIdentityNumberUniquenessChecker checker;

    @Test
    void ensureUniqueThrowsWhenExists() {
        when(userRepository.existsByIdentityNo("123")).thenReturn(true);
        assertThrows(GeneralException.class, () -> checker.ensureUnique("123"));
    }

    @Test
    void ensureUniqueDoesNothingWhenAvailable() {
        when(userRepository.existsByIdentityNo("123")).thenReturn(false);
        assertDoesNotThrow(() -> checker.ensureUnique("123"));
    }
}
