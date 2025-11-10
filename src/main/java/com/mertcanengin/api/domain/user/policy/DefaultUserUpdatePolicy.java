package com.mertcanengin.api.domain.user.policy;

import com.mertcanengin.api.domain.user.password.UserPasswordPolicy;
import com.mertcanengin.api.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DefaultUserUpdatePolicy implements UserUpdatePolicy {

    private final UserPasswordPolicy userPasswordPolicy;

    public DefaultUserUpdatePolicy(UserPasswordPolicy userPasswordPolicy) {
        this.userPasswordPolicy = userPasswordPolicy;
    }

    @Override
    public void apply(User incoming, User persisted) {
        if (!StringUtils.hasText(incoming.getPassword())) {
            incoming.setPassword(persisted.getPassword());
            return;
        }

        if (!incoming.getPassword().equals(persisted.getPassword())) {
            incoming.setPassword(userPasswordPolicy.encode(incoming.getPassword()));
        }
    }
}
