package com.mertcanengin.api.domain.user.validation;

import com.mertcanengin.api.common.GeneralException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DefaultIdentityNumberValidator implements IdentityNumberValidator {

    private static final int REQUIRED_LENGTH = 11;

    @Override
    public void validate(String identityNo) {
        if (!StringUtils.hasText(identityNo) || identityNo.length() != REQUIRED_LENGTH) {
            throw new GeneralException("Identity number must be exactly 11 characters long.");
        }
    }
}
