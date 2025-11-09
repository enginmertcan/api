package com.mertcanengin.api.service.impl;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.RefreshToken;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.repository.IRefreshTokenRepository;
import com.mertcanengin.api.repository.IUserRepository;
import com.mertcanengin.api.security.UserPrincipal;
import com.mertcanengin.api.service.IRefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class RefreshTokenService implements IRefreshTokenService {

    private final IRefreshTokenRepository refreshTokenRepository;
    private final IUserRepository userRepository;
    private final long refreshExpirationMinutes;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(IRefreshTokenRepository refreshTokenRepository,
                               IUserRepository userRepository,
                               @Value("${security.jwt.refresh-expiration-minutes:10080}") long refreshExpirationMinutes) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.refreshExpirationMinutes = refreshExpirationMinutes;
    }

    @Override
    @Transactional
    public String createToken(UserDetails userDetails) {
        User user = resolveUser(userDetails);
        revokeUserTokens(user.getId());
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(generateRandomToken());
        token.setExpiresAt(LocalDateTime.now().plusMinutes(refreshExpirationMinutes));
        refreshTokenRepository.save(token);
        return token.getToken();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails validateAndGetUser(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new GeneralException("Refresh token not found."));
        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new GeneralException("Refresh token is expired or revoked.");
        }
        return new UserPrincipal(refreshToken.getUser());
    }

    @Override
    @Transactional
    public String rotateToken(UserDetails userDetails, String oldToken) {
        refreshTokenRepository.findByToken(oldToken).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
        return createToken(userDetails);
    }

    @Override
    @Transactional
    public void revokeUserTokens(Integer userId) {
        refreshTokenRepository.deleteAllByUserId(userId);
    }

    private User resolveUser(UserDetails userDetails) {
        if (userDetails instanceof UserPrincipal principal) {
            return principal.getUser();
        }
        return userRepository.findByIdentityNo(userDetails.getUsername())
                .orElseThrow(() -> new GeneralException("User not found for refresh token."));
    }

    private String generateRandomToken() {
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
