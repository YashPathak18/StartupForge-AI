package com.startupforge.auth.service;

import com.startupforge.auth.exception.InvalidOtpException;
import com.startupforge.auth.exception.OtpRateLimitExceededException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Slf4j
@Service
public class OtpService {

    private static final String OTP_KEY_PREFIX = "otp:code:";
    private static final String ATTEMPTS_KEY_PREFIX = "otp:attempts:";
    private static final String COOLDOWN_KEY_PREFIX = "otp:cooldown:";

    private final RedisTemplate<String, String> redisTemplate;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${otp.length:6}")
    private int otpLength;

    @Value("${otp.expiry-seconds:300}")
    private long expirySeconds;

    @Value("${otp.resend-cooldown-seconds:60}")
    private long resendCooldownSeconds;

    @Value("${otp.max-attempts:5}")
    private int maxAttempts;

    @Value("${otp.log-to-console:false}")
    private boolean logToConsole;

    public OtpService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Generates a new OTP, stores it in Redis with a TTL, resets the attempt
     * counter, and starts the resend cooldown window. Does NOT send the email —
     * that's Notification Service's job (Phase 3), triggered by the
     * UserRegistered/OTP-resend event this OTP gets attached to.
     */
    public String generateOtp(String email) {
        String otp = generateNumericOtp();

        redisTemplate.opsForValue().set(otpKey(email), otp, Duration.ofSeconds(expirySeconds));
        redisTemplate.delete(attemptsKey(email));
        redisTemplate.opsForValue().set(cooldownKey(email), "1", Duration.ofSeconds(resendCooldownSeconds));

        if (logToConsole) {
            log.info("[DEV ONLY] Generated OTP for {}: {}", email, otp);
        }

        return otp;
    }

    /**
     * Throws OtpRateLimitExceededException if the caller must wait before
     * requesting another OTP for this email.
     */
    public void assertCanResend(String email) {
        Boolean onCooldown = redisTemplate.hasKey(cooldownKey(email));
        if (Boolean.TRUE.equals(onCooldown)) {
            throw new OtpRateLimitExceededException(
                    "Please wait before requesting another OTP");
        }
    }

    /**
     * Verifies the provided OTP against the stored value. Increments a failure
     * counter on mismatch and locks out further attempts once maxAttempts is hit.
     * On success, both the OTP and attempt counter are cleared.
     */
    public void verifyOtp(String email, String candidateOtp) {
        String attemptsKey = attemptsKey(email);
        String currentAttemptsStr = redisTemplate.opsForValue().get(attemptsKey);
        int currentAttempts = currentAttemptsStr == null ? 0 : Integer.parseInt(currentAttemptsStr);

        if (currentAttempts >= maxAttempts) {
            throw new OtpRateLimitExceededException(
                    "Too many failed attempts. Please request a new OTP.");
        }

        String storedOtp = redisTemplate.opsForValue().get(otpKey(email));
        if (storedOtp == null) {
            throw new InvalidOtpException("OTP has expired or does not exist. Please request a new one.");
        }

        if (!storedOtp.equals(candidateOtp)) {
            redisTemplate.opsForValue().set(attemptsKey, String.valueOf(currentAttempts + 1),
                    Duration.ofSeconds(expirySeconds));
            throw new InvalidOtpException("Invalid OTP");
        }

        redisTemplate.delete(otpKey(email));
        redisTemplate.delete(attemptsKey);
    }

    private String generateNumericOtp() {
        StringBuilder sb = new StringBuilder(otpLength);
        for (int i = 0; i < otpLength; i++) {
            sb.append(secureRandom.nextInt(10));
        }
        return sb.toString();
    }

    private String otpKey(String email) {
        return OTP_KEY_PREFIX + email;
    }

    private String attemptsKey(String email) {
        return ATTEMPTS_KEY_PREFIX + email;
    }

    private String cooldownKey(String email) {
        return COOLDOWN_KEY_PREFIX + email;
    }
}
