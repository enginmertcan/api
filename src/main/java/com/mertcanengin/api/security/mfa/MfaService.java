package com.mertcanengin.api.security.mfa;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.service.MailService;

@Service
public class MfaService {

    private static final String CHALLENGE_KEY_PREFIX = "auth:mfa:challenge:";

    private final StringRedisTemplate redisTemplate;
    private final MailService mailService;
    private final ObjectMapper objectMapper;
    private final Duration challengeTtl;
    private final int codeLength;
    private final SecureRandom random = new SecureRandom();

    public MfaService(StringRedisTemplate redisTemplate,
            MailService mailService,
            ObjectMapper objectMapper,
            @Value("${security.mfa.challenge-ttl-seconds:300}") long challengeTtlSeconds,
            @Value("${security.mfa.code-length:6}") int codeLength) {
        this.redisTemplate = redisTemplate;
        this.mailService = mailService;
        this.objectMapper = objectMapper;
        this.challengeTtl = Duration.ofSeconds(challengeTtlSeconds);
        this.codeLength = codeLength;
    }

    public MfaChallenge initiate(User user) {
        String challengeId = UUID.randomUUID().toString();
        String code = generateCode();
        Instant expiresAt = Instant.now().plus(challengeTtl);
        ChallengePayload payload = new ChallengePayload(
                user.getId(),
                user.getIdentityNo(),
                code,
                expiresAt.toEpochMilli());
        try {
            redisTemplate.opsForValue().set(
                    challengeKey(challengeId),
                    objectMapper.writeValueAsString(payload),
                    challengeTtl);
        } catch (JsonProcessingException e) {
            throw new GeneralException("MFA challenge oluşturulamadı.");
        }
        mailService.sendMfaCodeEmail(user.getEmail(), code, challengeTtl.toMinutes());
        return new MfaChallenge(
                challengeId,
                LocalDateTime.ofInstant(expiresAt, ZoneOffset.UTC),
                "EMAIL");
    }

    public void verify(String challengeId, String code, User user) {
        String payload = redisTemplate.opsForValue().get(challengeKey(challengeId));
        if (payload == null) {
            throw new GeneralException("MFA doğrulama isteği bulunamadı veya süresi doldu.");
        }
        ChallengePayload challenge = deserialize(payload);
        if (!challenge.userId().equals(user.getId())) {
            throw new GeneralException("MFA challenge bu kullanıcıya ait değil.");
        }
        if (!challenge.code().equals(code)) {
            throw new GeneralException("MFA kodu geçersiz.");
        }
        redisTemplate.delete(challengeKey(challengeId));
    }

    private String generateCode() {
        StringBuilder builder = new StringBuilder(codeLength);
        for (int i = 0; i < codeLength; i++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }

    private ChallengePayload deserialize(String payload) {
        try {
            return objectMapper.readValue(payload, ChallengePayload.class);
        } catch (JsonProcessingException e) {
            throw new GeneralException("MFA challenge okunamadı.");
        }
    }

    private String challengeKey(String challengeId) {
        return CHALLENGE_KEY_PREFIX + challengeId;
    }

    private record ChallengePayload(
            Integer userId,
            String identityNo,
            String code,
            long expiresAt) {
    }
}
