package com.mertcanengin.api.domain.user.validation;

public interface EmailUniquenessChecker {
    void ensureUnique(String email, Integer excludeUserId);
}
