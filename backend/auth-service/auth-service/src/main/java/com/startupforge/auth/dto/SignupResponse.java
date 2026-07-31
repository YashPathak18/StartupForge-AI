package com.startupforge.auth.dto;

import lombok.Builder;

@Builder
public record SignupResponse(
        String message,
        String email
) {
    public static SignupResponse of(String email) {
        return SignupResponse.builder()
                .message("Signup successful. Check your email for the OTP to verify your account.")
                .email(email)
                .build();
    }
}
