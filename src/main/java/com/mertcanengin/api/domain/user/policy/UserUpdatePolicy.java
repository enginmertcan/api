package com.mertcanengin.api.domain.user.policy;

import com.mertcanengin.api.entity.User;

public interface UserUpdatePolicy {
    void apply(User incoming, User persisted);
}
