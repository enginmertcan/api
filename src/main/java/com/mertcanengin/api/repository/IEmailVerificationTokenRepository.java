package com.mertcanengin.api.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mertcanengin.api.entity.EmailVerificationToken;

@Repository
public interface IEmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Integer> {
    Optional<EmailVerificationToken> findByUser_IdAndCodeAndConsumedFalse(Integer userId, String code);
    void deleteAllByUser_Id(Integer userId);
    void deleteAllByExpiresAtBefore(LocalDateTime threshold);
}
