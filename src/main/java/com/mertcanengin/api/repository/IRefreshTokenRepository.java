package com.mertcanengin.api.repository;

import com.mertcanengin.api.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IRefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("delete from RefreshToken rt where rt.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Integer userId);
}
