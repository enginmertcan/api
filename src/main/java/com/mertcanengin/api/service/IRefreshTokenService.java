package com.mertcanengin.api.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface IRefreshTokenService {
    String createToken(UserDetails userDetails);
    UserDetails validateAndGetUser(String token);
    String rotateToken(UserDetails userDetails, String oldToken);
    void revokeUserTokens(Integer userId);
}
