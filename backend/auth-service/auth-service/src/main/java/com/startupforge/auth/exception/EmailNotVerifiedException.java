package com.startupforge.auth.exception;

public class EmailNotVerifiedException extends RuntimeException {
    public EmailNotVerifiedException() {
        super("Email not verified. Please verify your account using the OTP sent to your email.");
    }
}
