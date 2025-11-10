package com.mertcanengin.api.domain.user.policy;

import com.mertcanengin.api.domain.user.password.UserPasswordPolicy;
import com.mertcanengin.api.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultUserUpdatePolicyTest {

    @Mock
    private UserPasswordPolicy userPasswordPolicy;

    @InjectMocks
    private DefaultUserUpdatePolicy policy;

    private User incoming;
    private User existing;

    @BeforeEach
    void setUp() {
        incoming = new User();
        existing = new User();
        existing.setPassword("encoded");
    }

    @Test
    void applyKeepsExistingPasswordWhenBlank() {
        incoming.setPassword(" ");

        policy.apply(incoming, existing);

        assertEquals("encoded", incoming.getPassword());
        verifyNoInteractions(userPasswordPolicy);
    }

    @Test
    void applyEncodesWhenChanged() {
        incoming.setPassword("Aa1!aaaa");
        when(userPasswordPolicy.encode("Aa1!aaaa")).thenReturn("newEncoded");

        policy.apply(incoming, existing);

        assertEquals("newEncoded", incoming.getPassword());
    }
}
