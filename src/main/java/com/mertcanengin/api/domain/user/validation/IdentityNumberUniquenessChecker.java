package com.mertcanengin.api.domain.user.validation;

public interface IdentityNumberUniquenessChecker {
    void ensureUnique(String identityNo);
}
