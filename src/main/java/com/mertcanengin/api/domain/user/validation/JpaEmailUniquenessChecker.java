package com.mertcanengin.api.domain.user.validation;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.repository.IUserRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JpaEmailUniquenessChecker implements EmailUniquenessChecker {

    private final IUserRepository userRepository;

    public JpaEmailUniquenessChecker(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void ensureUnique(String email, Integer excludeUserId) {
        if (!StringUtils.hasText(email)) {
            throw new GeneralException("Email address is required.");
        }
        boolean exists;
        if (excludeUserId == null) {
            exists = userRepository.existsByEmail(email);
        } else {
            exists = userRepository.existsByEmailAndIdNot(email, excludeUserId);
        }
        if (exists) {
            throw new GeneralException("A user with this email already exists.");
        }
    }
}
