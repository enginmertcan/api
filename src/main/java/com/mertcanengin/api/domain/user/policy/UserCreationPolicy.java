package com.mertcanengin.api.domain.user.policy;

import com.mertcanengin.api.entity.User;

public interface UserCreationPolicy {
    void apply(User user);
}
