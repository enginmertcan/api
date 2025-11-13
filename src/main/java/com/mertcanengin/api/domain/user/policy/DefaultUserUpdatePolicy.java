package com.mertcanengin.api.domain.user.policy;

import com.mertcanengin.api.domain.user.password.UserPasswordPolicy;
import com.mertcanengin.api.domain.user.validation.EmailUniquenessChecker;
import com.mertcanengin.api.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DefaultUserUpdatePolicy implements UserUpdatePolicy {

    private final UserPasswordPolicy userPasswordPolicy;
    private final EmailUniquenessChecker emailUniquenessChecker;

    public DefaultUserUpdatePolicy(UserPasswordPolicy userPasswordPolicy,
                                   EmailUniquenessChecker emailUniquenessChecker) {
        this.userPasswordPolicy = userPasswordPolicy;
        this.emailUniquenessChecker = emailUniquenessChecker;
    }

    @Override
    public void apply(User incoming, User persisted) {
        if (StringUtils.hasText(incoming.getEmail()) && !incoming.getEmail().equalsIgnoreCase(persisted.getEmail())) {
            emailUniquenessChecker.ensureUnique(incoming.getEmail(), persisted.getId());
        } else {
            incoming.setEmail(persisted.getEmail());
        }

        if (!StringUtils.hasText(incoming.getPassword())) {
            incoming.setPassword(persisted.getPassword());
            return;
        }

        if (!incoming.getPassword().equals(persisted.getPassword())) {
            incoming.setPassword(userPasswordPolicy.encode(incoming.getPassword()));
        }
    }
}
