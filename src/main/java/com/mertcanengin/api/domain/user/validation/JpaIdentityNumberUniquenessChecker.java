package com.mertcanengin.api.domain.user.validation;

import org.springframework.stereotype.Component;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.repository.IUserRepository;

@Component
public class JpaIdentityNumberUniquenessChecker implements IdentityNumberUniquenessChecker {

    private final IUserRepository userRepository;

    public JpaIdentityNumberUniquenessChecker(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void ensureUnique(String identityNo) {
        if (userRepository.existsByIdentityNo(identityNo)) {
            throw new GeneralException("A user with this identity number already exists.");
        }
    }
}
