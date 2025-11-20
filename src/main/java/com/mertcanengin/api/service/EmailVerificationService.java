package com.mertcanengin.api.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.EmailVerificationToken;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.repository.IEmailVerificationTokenRepository;
import com.mertcanengin.api.repository.IUserRepository;

@Service
public class EmailVerificationService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int CODE_LENGTH = 6;
    private static final int EXPIRATION_MINUTES = 15;

    private final IEmailVerificationTokenRepository tokenRepository;
    private final IUserRepository userRepository;
    private final MailService mailService;

    public EmailVerificationService(IEmailVerificationTokenRepository tokenRepository,
                                    IUserRepository userRepository,
                                    MailService mailService) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    @Transactional
    public void startVerification(User user) {
        tokenRepository.deleteAllByUser_Id(user.getId());
        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(user);
        token.setCode(generateCode());
        token.setExpiresAt(LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES));
        tokenRepository.save(token);
        mailService.sendVerificationEmail(user.getEmail(), token.getCode());
    }

    @Transactional
    public User verifyCode(String identityNo, String code) {
        User user = userRepository.findByIdentityNo(identityNo)
                .orElseThrow(() -> new GeneralException("Kullanıcı bulunamadı."));
        if (user.isEmailVerified()) {
            throw new GeneralException("Bu hesap zaten doğrulanmış.");
        }
        EmailVerificationToken token = tokenRepository.findByUser_IdAndCodeAndConsumedFalse(user.getId(), code)
                .orElseThrow(() -> new GeneralException("Geçersiz doğrulama kodu."));
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            tokenRepository.deleteAllByUser_Id(user.getId());
            throw new GeneralException("Doğrulama kodunun süresi dolmuş. Lütfen yeni bir kod isteyin.");
        }
        token.setConsumed(true);
        tokenRepository.save(token);
        user.setEmailVerified(true);
        userRepository.save(user);
        tokenRepository.deleteAllByUser_Id(user.getId());
        return user;
    }

    @Transactional
    public void resendCode(String identityNo) {
        User user = userRepository.findByIdentityNo(identityNo)
                .orElseThrow(() -> new GeneralException("Kullanıcı bulunamadı."));
        if (user.isEmailVerified()) {
            throw new GeneralException("Bu hesap zaten doğrulanmış.");
        }
        startVerification(user);
    }

    public void purgeExpiredTokens() {
        tokenRepository.deleteAllByExpiresAtBefore(LocalDateTime.now().minusDays(1));
    }

    private String generateCode() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            builder.append(RANDOM.nextInt(10));
        }
        return builder.toString();
    }
}
