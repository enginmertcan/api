package com.mertcanengin.api.service.impl;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.domain.user.policy.UserCreationPolicy;
import com.mertcanengin.api.domain.user.policy.UserDeletionGuard;
import com.mertcanengin.api.domain.user.policy.UserUpdatePolicy;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.repository.IUserRepository;
import com.mertcanengin.api.service.IRefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private IUserRepository userRepository;
    @Mock
    private IRefreshTokenService refreshTokenService;
    @Mock
    private UserCreationPolicy userCreationPolicy;
    @Mock
    private UserUpdatePolicy userUpdatePolicy;
    @Mock
    private UserDeletionGuard userDeletionGuard;

    @InjectMocks
    private UserService userService;

    @Test
    void saveForNewUserUsesCreationPolicy() {
        User user = new User();
        when(userRepository.save(user)).thenReturn(user);

        User saved = userService.save(user);

        assertEquals(user, saved);
        verify(userCreationPolicy).apply(user);
        verify(userRepository).save(user);
    }

    @Test
    void saveForExistingUserUsesUpdatePolicy() {
        User user = new User();
        user.setId(1);

        User persisted = new User();
        persisted.setId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(persisted));
        when(userRepository.save(user)).thenReturn(user);

        userService.save(user);

        verify(userUpdatePolicy).apply(user, persisted);
    }

    @Test
    void saveThrowsWhenUserNull() {
        assertThrows(GeneralException.class, () -> userService.save(null));
        verifyNoInteractions(userRepository, userCreationPolicy, userUpdatePolicy);
    }

    @Test
    void deleteInvokesGuardAndRevokesTokens() {
        User user = new User();
        user.setId(3);
        when(userRepository.findById(3)).thenReturn(Optional.of(user));

        userService.delete(3);

        verify(userDeletionGuard).ensureCanDelete(user);
        verify(refreshTokenService).revokeUserTokens(3);
        verify(userRepository).deleteById(3);
    }
}
