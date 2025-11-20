package com.mertcanengin.api.service.impl;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.domain.user.policy.UserCreationPolicy;
import com.mertcanengin.api.domain.user.policy.UserDeletionGuard;
import com.mertcanengin.api.domain.user.policy.UserUpdatePolicy;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.repository.IUserRepository;
import com.mertcanengin.api.service.IRefreshTokenService;

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
        Mockito.when(userRepository.save(user)).thenReturn(user);

        User saved = userService.save(user);

        Assertions.assertEquals(user, saved);
        Mockito.verify(userCreationPolicy).apply(user);
        Mockito.verify(userRepository).save(user);
    }

    @Test
    void saveForExistingUserUsesUpdatePolicy() {
        User user = new User();
        user.setId(1);

        User persisted = new User();
        persisted.setId(1);

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(persisted));
        Mockito.when(userRepository.save(user)).thenReturn(user);

        userService.save(user);

        Mockito.verify(userUpdatePolicy).apply(user, persisted);
    }

    @Test
    void saveThrowsWhenUserNull() {
        Assertions.assertThrows(GeneralException.class, () -> userService.save(null));
        Mockito.verifyNoInteractions(userRepository, userCreationPolicy, userUpdatePolicy);
    }

    @Test
    void deleteInvokesGuardAndRevokesTokens() {
        User user = new User();
        user.setId(3);
        Mockito.when(userRepository.findById(3)).thenReturn(Optional.of(user));

        userService.delete(3);

        Mockito.verify(userDeletionGuard).ensureCanDelete(user);
        Mockito.verify(refreshTokenService).revokeUserTokens(3);
        Mockito.verify(userRepository).deleteById(3);
    }
}
