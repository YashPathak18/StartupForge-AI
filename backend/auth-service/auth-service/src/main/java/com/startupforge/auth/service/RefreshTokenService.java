package com.startupforge.auth.service;

import com.startupforge.auth.config.JwtProperties;
import com.startupforge.auth.entity.RefreshToken;
import com.startupforge.auth.exception.InvalidRefreshTokenException;
import com.startupforge.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

/**
 * Refresh tokens are opaque random strings (not JWTs) stored server-side in
 * Postgres, not Redis - unlike OTPs, they need to be revocable on demand
 * (logout, security event) and durable across a Redis restart, which is
 * exactly the tradeoff a relational table is suited for over a cache.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public String issue(UUID userId) {
        String token = generateOpaqueToken();

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .token(token)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtProperties.getRefreshTokenExpiryMs() / 1000))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
        return token;
    }

    /**
     * Validates the refresh token, revokes it (rotation - one-time use), and
     * returns the associated userId so the caller can issue new tokens.
     */
    @Transactional
    public UUID validateAndRotate(String token) {
        RefreshToken stored = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not recognized"));

        if (stored.isRevoked()) {
            throw new InvalidRefreshTokenException("Refresh token has already been used or revoked");
        }
        if (stored.isExpired()) {
            throw new InvalidRefreshTokenException("Refresh token has expired");
        }

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        return stored.getUserId();
    }

    @Transactional
    public void revokeAllForUser(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    private String generateOpaqueToken() {
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
