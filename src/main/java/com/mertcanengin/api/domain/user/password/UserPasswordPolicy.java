package com.mertcanengin.api.domain.user.password;

public interface UserPasswordPolicy {
    String encode(String rawPassword);
}
