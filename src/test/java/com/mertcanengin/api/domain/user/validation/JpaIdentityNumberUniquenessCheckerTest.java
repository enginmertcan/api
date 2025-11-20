package com.mertcanengin.api.domain.user.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.repository.IUserRepository;

@ExtendWith(MockitoExtension.class)
class JpaIdentityNumberUniquenessCheckerTest {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private JpaIdentityNumberUniquenessChecker checker;

    @Test
    void ensureUniqueThrowsWhenExists() {
        Mockito.when(userRepository.existsByIdentityNo("123")).thenReturn(true);
        Assertions.assertThrows(GeneralException.class, () -> checker.ensureUnique("123"));
    }

    @Test
    void ensureUniqueDoesNothingWhenAvailable() {
        Mockito.when(userRepository.existsByIdentityNo("123")).thenReturn(false);
        Assertions.assertDoesNotThrow(() -> checker.ensureUnique("123"));
    }
}
