package com.mertcanengin.api.security.password;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.repository.IUserRepository;
import com.mertcanengin.api.service.IRefreshTokenService;
import com.mertcanengin.api.service.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class PasswordResetService {

    private static final String RESET_KEY_PREFIX = "auth:password-reset:";

    private final StringRedisTemplate redisTemplate;
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IRefreshTokenService refreshTokenService;
    private final MailService mailService;
    private final ObjectMapper objectMapper;
    private final Duration tokenTtl;

    public PasswordResetService(StringRedisTemplate redisTemplate,
                                IUserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                IRefreshTokenService refreshTokenService,
                                MailService mailService,
                                ObjectMapper objectMapper,
                                @Value("${security.password-reset.token-ttl-minutes:30}") long ttlMinutes) {
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.mailService = mailService;
        this.objectMapper = objectMapper;
        this.tokenTtl = Duration.ofMinutes(ttlMinutes);
    }

    public void requestReset(String identityNo, String email) {
        User user = userRepository.findByIdentityNo(identityNo)
                .orElseThrow(() -> new GeneralException("Kullanıcı bulunamadı."));
        if (email != null && !email.equalsIgnoreCase(user.getEmail())) {
            throw new GeneralException("E-posta bilgisi eşleşmiyor.");
        }
        String token = UUID.randomUUID().toString();
        ResetPayload payload = new ResetPayload(user.getId(), Instant.now().plus(tokenTtl).toEpochMilli());
        try {
            redisTemplate.opsForValue().set(resetKey(token), objectMapper.writeValueAsString(payload), tokenTtl);
        } catch (JsonProcessingException e) {
            throw new GeneralException("Parola sıfırlama isteği oluşturulamadı.");
        }
        mailService.sendPasswordResetEmail(user.getEmail(), token, tokenTtl.toMinutes());
    }

    public void resetPassword(String token, String newPassword) {
        String payload = redisTemplate.opsForValue().get(resetKey(token));
        if (payload == null) {
            throw new GeneralException("Parola sıfırlama isteğinin süresi dolmuş.");
        }
        ResetPayload data = deserialize(payload);
        if (Instant.ofEpochMilli(data.expiresAt()).isBefore(Instant.now())) {
            redisTemplate.delete(resetKey(token));
            throw new GeneralException("Parola sıfırlama isteğinin süresi dolmuş.");
        }
        User user = userRepository.findById(data.userId())
                .orElseThrow(() -> new GeneralException("Kullanıcı bulunamadı."));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        refreshTokenService.revokeUserTokens(user.getId());
        redisTemplate.delete(resetKey(token));
    }

    private ResetPayload deserialize(String payload) {
        try {
            return objectMapper.readValue(payload, ResetPayload.class);
        } catch (JsonProcessingException e) {
            throw new GeneralException("Parola sıfırlama isteği okunamadı.");
        }
    }

    private String resetKey(String token) {
        return RESET_KEY_PREFIX + token;
    }

    private record ResetPayload(Integer userId, long expiresAt) {
    }
}

