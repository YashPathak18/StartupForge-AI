package com.startupforge.auth.controller;

import com.startupforge.auth.dto.OtpResendRequest;
import com.startupforge.auth.dto.OtpVerifyRequest;
import com.startupforge.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth/otp")
@RequiredArgsConstructor
public class OtpController {

    private final AuthService authService;

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verify(@Valid @RequestBody OtpVerifyRequest request) {
        authService.verifyOtp(request);
        return ResponseEntity.ok(Map.of("message", "Email verified successfully. You can now log in."));
    }

    @PostMapping("/resend")
    public ResponseEntity<Map<String, String>> resend(@Valid @RequestBody OtpResendRequest request) {
        authService.resendOtp(request);
        return ResponseEntity.ok(Map.of("message", "A new OTP has been sent to your email."));
    }
}
