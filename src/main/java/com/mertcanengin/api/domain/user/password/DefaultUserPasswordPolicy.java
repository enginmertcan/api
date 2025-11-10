package com.mertcanengin.api.domain.user.password;

import com.mertcanengin.api.common.GeneralException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@Component
public class DefaultUserPasswordPolicy implements UserPasswordPolicy {

    private static final Pattern STRONG_PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");
    private static final String PASSWORD_POLICY_MESSAGE =
            "Password must be at least 8 characters and include upper, lower, numeric and special characters.";

    private final PasswordEncoder passwordEncoder;

    public DefaultUserPasswordPolicy(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(String rawPassword) {
        if (!StringUtils.hasText(rawPassword) || !STRONG_PASSWORD_PATTERN.matcher(rawPassword).matches()) {
            throw new GeneralException(PASSWORD_POLICY_MESSAGE);
        }
        return passwordEncoder.encode(rawPassword);
    }
}
