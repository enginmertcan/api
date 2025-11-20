package com.mertcanengin.api.domain.user.policy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mertcanengin.api.domain.user.password.UserPasswordPolicy;
import com.mertcanengin.api.domain.user.validation.EmailUniquenessChecker;
import com.mertcanengin.api.domain.user.validation.IdentityNumberUniquenessChecker;
import com.mertcanengin.api.domain.user.validation.IdentityNumberValidator;
import com.mertcanengin.api.entity.User;

@ExtendWith(MockitoExtension.class)
class DefaultUserCreationPolicyTest {

    @Mock
    private IdentityNumberValidator identityNumberValidator;
    @Mock
    private IdentityNumberUniquenessChecker identityNumberUniquenessChecker;
    @Mock
    private EmailUniquenessChecker emailUniquenessChecker;
    @Mock
    private UserPasswordPolicy userPasswordPolicy;

    @InjectMocks
    private DefaultUserCreationPolicy policy;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setIdentityNo("12345678901");
        user.setEmail("user@example.com");
        user.setPassword("Aa1!aaaa");
    }

    @Test
    void applyValidatesAndEncodesPassword() {
        Mockito.when(userPasswordPolicy.encode(user.getPassword())).thenReturn("encoded");

        policy.apply(user);

        Mockito.verify(identityNumberValidator).validate(user.getIdentityNo());
        Mockito.verify(identityNumberUniquenessChecker).ensureUnique(user.getIdentityNo());
        Mockito.verify(emailUniquenessChecker).ensureUnique(user.getEmail(), null);
        Assertions.assertEquals("encoded", user.getPassword());
    }
}
