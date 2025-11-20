package com.mertcanengin.api.service;

import com.mertcanengin.api.security.session.DeviceMetadata;
import com.mertcanengin.api.security.session.RefreshSession;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface IRefreshTokenService {

    String createToken(UserDetails userDetails, DeviceMetadata deviceMetadata);

    UserDetails validateAndGetUser(String token);

    String rotateToken(UserDetails userDetails, String oldToken, DeviceMetadata deviceMetadata);

    RefreshSession getSession(String token);

    void revokeSession(Integer userId, String deviceId);

    List<RefreshSession> getActiveSessions(Integer userId);

    void revokeUserTokens(Integer userId);
}
