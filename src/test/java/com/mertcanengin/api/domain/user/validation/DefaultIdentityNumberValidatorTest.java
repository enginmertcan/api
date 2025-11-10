package com.mertcanengin.api.domain.user.validation;

import com.mertcanengin.api.common.GeneralException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultIdentityNumberValidatorTest {

    private DefaultIdentityNumberValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DefaultIdentityNumberValidator();
    }

    @Test
    void validatePassesForValidNumber() {
        assertDoesNotThrow(() -> validator.validate("12345678901"));
    }

    @Test
    void validateFailsForWrongLength() {
        assertThrows(GeneralException.class, () -> validator.validate("123"));
    }
}
