package com.mertcanengin.api.domain.user.password;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mertcanengin.api.common.GeneralException;

class DefaultUserPasswordPolicyTest {

    private PasswordEncoder passwordEncoder;
    private DefaultUserPasswordPolicy policy;

    @BeforeEach
    void setUp() {
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        policy = new DefaultUserPasswordPolicy(passwordEncoder);
    }

    @Test
    void encodeReturnsHashedPasswordWhenValid() {
        Mockito.when(passwordEncoder.encode("Aa1!aaaa")).thenReturn("encoded");
        String encoded = policy.encode("Aa1!aaaa");
        Assertions.assertEquals("encoded", encoded);
    }

    @Test
    void encodeThrowsWhenPasswordWeak() {
        Assertions.assertThrows(GeneralException.class, () -> policy.encode("weak"));
    }
}
