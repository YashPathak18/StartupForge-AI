package com.startupforge.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.startupforge.auth.exception.InvalidGoogleTokenException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * Verifies Google ID tokens sent by the frontend after it performs Google
 * Sign-In client-side. We never see the user's Google password and never
 * redirect - the frontend hands us a signed token, we verify it against
 * Google's public keys and check the audience matches our client ID.
 */
@Slf4j
@Service
public class GoogleAuthService {

    @Value("${google.client-id}")
    private String googleClientId;

    private GoogleIdTokenVerifier verifier;

    @PostConstruct
    private void init() {
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
    }

    public GoogleIdToken.Payload verify(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new InvalidGoogleTokenException("Google ID token is invalid or expired");
            }
            return idToken.getPayload();
        } catch (GeneralSecurityException | java.io.IOException e) {
            log.error("Failed to verify Google ID token", e);
            throw new InvalidGoogleTokenException("Could not verify Google ID token");
        }
    }
}
