package com.startupforge.auth.service;

import com.startupforge.auth.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Deliberately a plain unit test, not @SpringBootTest - a full context load
 * would require live Postgres/Redis/RabbitMQ connections that don't exist
 * until real credentials are wired into .env, which would break `mvn test`
 * for anyone cloning the repo fresh. JwtService has no such dependency, so
 * it's tested directly.
 */
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("test-secret-key-that-is-long-enough-for-hs256-signing-1234567890");
        properties.setAccessTokenExpiryMs(900000L);
        properties.setRefreshTokenExpiryMs(604800000L);

        jwtService = new JwtService(properties);
    }

    @Test
    void generatesTokenThatIsValidAndCarriesExpectedClaims() {
        UUID userId = UUID.randomUUID();
        String email = "founder@startupforge.dev";
        String role = "USER";

        String token = jwtService.generateAccessToken(userId, email, role);

        assertTrue(jwtService.isTokenValid(token));
        assertEquals(userId, jwtService.extractUserId(token));
        assertEquals(email, jwtService.extractEmail(token));
        assertEquals(role, jwtService.extractRole(token));
    }

    @Test
    void rejectsTamperedToken() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateAccessToken(userId, "a@b.com", "USER");

        String tampered = token.substring(0, token.length() - 2) + "xx";

        assertFalse(jwtService.isTokenValid(tampered));
    }
}
