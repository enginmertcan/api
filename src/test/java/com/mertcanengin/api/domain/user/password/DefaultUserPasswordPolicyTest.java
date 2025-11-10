package com.mertcanengin.api.domain.user.password;

import com.mertcanengin.api.common.GeneralException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertEquals("encoded", encoded);
    }

    @Test
    void encodeThrowsWhenPasswordWeak() {
        assertThrows(GeneralException.class, () -> policy.encode("weak"));
    }
}
