package com.mertcanengin.api.domain.user.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mertcanengin.api.common.GeneralException;

class DefaultIdentityNumberValidatorTest {

    private DefaultIdentityNumberValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DefaultIdentityNumberValidator();
    }

    @Test
    void validatePassesForValidNumber() {
        Assertions.assertDoesNotThrow(() -> validator.validate("12345678901"));
    }

    @Test
    void validateFailsForWrongLength() {
        Assertions.assertThrows(GeneralException.class, () -> validator.validate("123"));
    }
}
