package com.mertcanengin.api.domain.user.policy;

import com.mertcanengin.api.domain.user.password.UserPasswordPolicy;
import com.mertcanengin.api.domain.user.validation.EmailUniquenessChecker;
import com.mertcanengin.api.domain.user.validation.IdentityNumberUniquenessChecker;
import com.mertcanengin.api.domain.user.validation.IdentityNumberValidator;
import com.mertcanengin.api.entity.User;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserCreationPolicy implements UserCreationPolicy {

    private final IdentityNumberValidator identityNumberValidator;
    private final IdentityNumberUniquenessChecker identityNumberUniquenessChecker;
    private final EmailUniquenessChecker emailUniquenessChecker;
    private final UserPasswordPolicy userPasswordPolicy;

    public DefaultUserCreationPolicy(IdentityNumberValidator identityNumberValidator,
                                     IdentityNumberUniquenessChecker identityNumberUniquenessChecker,
                                     EmailUniquenessChecker emailUniquenessChecker,
                                     UserPasswordPolicy userPasswordPolicy) {
        this.identityNumberValidator = identityNumberValidator;
        this.identityNumberUniquenessChecker = identityNumberUniquenessChecker;
        this.emailUniquenessChecker = emailUniquenessChecker;
        this.userPasswordPolicy = userPasswordPolicy;
    }

    @Override
    public void apply(User user) {
        identityNumberValidator.validate(user.getIdentityNo());
        identityNumberUniquenessChecker.ensureUnique(user.getIdentityNo());
        emailUniquenessChecker.ensureUnique(user.getEmail(), null);
        user.setPassword(userPasswordPolicy.encode(user.getPassword()));
    }
}
