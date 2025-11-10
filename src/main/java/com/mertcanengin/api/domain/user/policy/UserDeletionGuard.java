package com.mertcanengin.api.domain.user.policy;

import com.mertcanengin.api.entity.User;

public interface UserDeletionGuard {
    void ensureCanDelete(User user);
}
