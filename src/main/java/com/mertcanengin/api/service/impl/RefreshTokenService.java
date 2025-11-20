package com.mertcanengin.api.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.repository.IUserRepository;
import com.mertcanengin.api.security.UserPrincipal;
import com.mertcanengin.api.security.session.DeviceMetadata;
import com.mertcanengin.api.security.session.RefreshSession;
import com.mertcanengin.api.service.IRefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RefreshTokenService implements IRefreshTokenService {

    private static final String TOKEN_KEY_PREFIX = "auth:refresh:token:";
    private static final String USER_SESSIONS_KEY_PREFIX = "auth:refresh:user:";
    private static final String BLACKLIST_KEY_PREFIX = "auth:refresh:blacklist:";

    private final StringRedisTemplate redisTemplate;
    private final IUserRepository userRepository;
    private final Duration refreshExpiration;
    private final int maxDevicesPerUser;
    private final ObjectMapper objectMapper;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(StringRedisTemplate redisTemplate,
                               IUserRepository userRepository,
                               ObjectMapper objectMapper,
                               @Value("${security.jwt.refresh-expiration-minutes:10080}") long refreshExpirationMinutes,
                               @Value("${security.sessions.max-devices-per-user:10}") int maxDevicesPerUser) {
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.refreshExpiration = Duration.ofMinutes(refreshExpirationMinutes);
        this.maxDevicesPerUser = maxDevicesPerUser;
    }

    @Override
    @Transactional
    public String createToken(UserDetails userDetails, DeviceMetadata deviceMetadata) {
        User user = resolveUser(userDetails);
        DeviceMetadata metadata = normalizeMetadata(deviceMetadata);
        revokeDeviceSessions(user.getId(), metadata.deviceId());
        String token = generateRandomToken();
        storeSession(user, token, metadata);
        enforceDeviceLimit(user.getId());
        return token;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails validateAndGetUser(String token) {
        RefreshSession session = requireSession(token);
        User user = userRepository.findById(session.userId())
                .orElseThrow(() -> new GeneralException("Kullanıcı bulunamadı."));
        return new UserPrincipal(user);
    }

    @Override
    @Transactional
    public String rotateToken(UserDetails userDetails, String oldToken, DeviceMetadata deviceMetadata) {
        RefreshSession existing = requireSession(oldToken);
        blacklistToken(existing.token());
        DeviceMetadata metadata = deviceMetadata != null ? normalizeMetadata(deviceMetadata) : existing.deviceMetadata();
        return createToken(userDetails, metadata);
    }

    @Override
    public RefreshSession getSession(String token) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey(token)))) {
            throw new GeneralException("Refresh token revoked.");
        }
        String payload = redisTemplate.opsForValue().get(tokenKey(token));
        if (payload == null) {
            throw new GeneralException("Refresh token not found.");
        }
        TokenPayload tokenPayload = deserialize(payload);
        LocalDateTime expiresAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(tokenPayload.expiresAtEpochMs()), ZoneOffset.UTC);
        if (expiresAt.isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
            blacklistToken(token);
            throw new GeneralException("Refresh token expired.");
        }
        LocalDateTime issuedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(tokenPayload.issuedAtEpochMs()), ZoneOffset.UTC);
        return new RefreshSession(
                token,
                tokenPayload.userId(),
                tokenPayload.identityNo(),
                tokenPayload.deviceId(),
                tokenPayload.deviceName(),
                tokenPayload.ipAddress(),
                tokenPayload.userAgent(),
                issuedAt,
                expiresAt
        );
    }

    @Override
    public void revokeSession(Integer userId, String deviceId) {
        if (userId == null || deviceId == null) {
            return;
        }
        List<RefreshSession> sessions = getActiveSessions(userId);
        sessions.stream()
                .filter(session -> deviceId.equals(session.deviceId()))
                .forEach(session -> blacklistToken(session.token()));
    }

    @Override
    public List<RefreshSession> getActiveSessions(Integer userId) {
        String key = userSessionsKey(userId);
        Set<String> tokens = redisTemplate.opsForZSet().range(key, 0, -1);
        if (tokens == null || tokens.isEmpty()) {
            return List.of();
        }
        return tokens.stream()
                .map(token -> {
                    try {
                        return Optional.of(getSession(token));
                    } catch (GeneralException ignored) {
                        redisTemplate.opsForZSet().remove(key, token);
                        return Optional.<RefreshSession>empty();
                    }
                })
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    @Override
    public void revokeUserTokens(Integer userId) {
        if (userId == null) {
            return;
        }
        List<RefreshSession> sessions = getActiveSessions(userId);
        sessions.forEach(session -> blacklistToken(session.token()));
        redisTemplate.delete(userSessionsKey(userId));
    }

    private RefreshSession requireSession(String token) {
        try {
            return getSession(token);
        } catch (GeneralException ex) {
            throw ex;
        }
    }

    private void storeSession(User user, String token, DeviceMetadata metadata) {
        Instant now = Instant.now();
        Instant expires = now.plus(refreshExpiration);
        TokenPayload payload = new TokenPayload(
                user.getId(),
                user.getIdentityNo(),
                metadata.deviceId(),
                metadata.deviceName(),
                metadata.ipAddress(),
                metadata.userAgent(),
                now.toEpochMilli(),
                expires.toEpochMilli()
        );
        try {
            String serialized = objectMapper.writeValueAsString(payload);
            redisTemplate.opsForValue().set(tokenKey(token), serialized, refreshExpiration);
        } catch (JsonProcessingException e) {
            throw new GeneralException("Refresh token saklanamadı.");
        }
        long score = now.toEpochMilli();
        redisTemplate.opsForZSet().add(userSessionsKey(user.getId()), token, score);
        redisTemplate.expire(userSessionsKey(user.getId()), refreshExpiration);
    }

    private void revokeDeviceSessions(Integer userId, String deviceId) {
        if (deviceId == null) {
            return;
        }
        List<RefreshSession> sessions = getActiveSessions(userId);
        sessions.stream()
                .filter(session -> Objects.equals(deviceId, session.deviceId()))
                .forEach(session -> blacklistToken(session.token()));
    }

    private void enforceDeviceLimit(Integer userId) {
        String key = userSessionsKey(userId);
        ZSetOperations<String, String> zSet = redisTemplate.opsForZSet();
        Long size = zSet.size(key);
        if (size == null || size <= maxDevicesPerUser) {
            return;
        }
        long excess = size - maxDevicesPerUser;
        Set<String> tokensToRemove = zSet.range(key, 0, excess - 1);
        if (tokensToRemove == null) {
            return;
        }
        tokensToRemove.forEach(this::blacklistToken);
    }

    private void blacklistToken(String token) {
        if (token == null) {
            return;
        }
        TokenPayload existingPayload = null;
        String existingJson = redisTemplate.opsForValue().get(tokenKey(token));
        if (existingJson != null) {
            try {
                existingPayload = objectMapper.readValue(existingJson, TokenPayload.class);
            } catch (JsonProcessingException ignored) {
            }
        }
        String tokenKey = tokenKey(token);
        Long ttl = redisTemplate.getExpire(tokenKey);
        redisTemplate.delete(tokenKey);
        redisTemplate.opsForValue().set(
                blacklistKey(token),
                "revoked",
                ttl != null && ttl > 0 ? Duration.ofSeconds(ttl) : refreshExpiration
        );
        if (existingPayload != null) {
            redisTemplate.opsForZSet().remove(userSessionsKey(existingPayload.userId()), token);
        }
    }

    private DeviceMetadata normalizeMetadata(DeviceMetadata metadata) {
        if (metadata == null) {
            return DeviceMetadata.of(null, null, null, null);
        }
        return DeviceMetadata.of(metadata.deviceId(), metadata.deviceName(), metadata.ipAddress(), metadata.userAgent());
    }

    private User resolveUser(UserDetails userDetails) {
        if (userDetails instanceof UserPrincipal principal) {
            return principal.getUser();
        }
        return userRepository.findByIdentityNo(userDetails.getUsername())
                .orElseThrow(() -> new GeneralException("User not found for refresh token."));
    }

    private TokenPayload deserialize(String payload) {
        try {
            return objectMapper.readValue(payload, TokenPayload.class);
        } catch (JsonProcessingException e) {
            throw new GeneralException("Refresh token okunamadı.");
        }
    }

    private String tokenKey(String token) {
        return TOKEN_KEY_PREFIX + token;
    }

    private String userSessionsKey(Integer userId) {
        return USER_SESSIONS_KEY_PREFIX + userId;
    }

    private String blacklistKey(String token) {
        return BLACKLIST_KEY_PREFIX + token;
    }

    private String generateRandomToken() {
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private record TokenPayload(
            Integer userId,
            String identityNo,
            String deviceId,
            String deviceName,
            String ipAddress,
            String userAgent,
            long issuedAtEpochMs,
            long expiresAtEpochMs
    ) {
    }
}
