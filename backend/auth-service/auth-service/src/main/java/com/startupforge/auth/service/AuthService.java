package com.startupforge.auth.service;

import com.startupforge.auth.config.JwtProperties;
import com.startupforge.auth.dto.*;
import com.startupforge.auth.entity.AuthProvider;
import com.startupforge.auth.entity.Role;
import com.startupforge.auth.entity.User;
import com.startupforge.auth.event.UserRegisteredEvent;
import com.startupforge.auth.exception.*;
import com.startupforge.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final OtpService otpService;
    private final UserEventPublisher userEventPublisher;
    private final JwtProperties jwtProperties;
    private final GoogleAuthService googleAuthService;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .authProvider(AuthProvider.LOCAL)
                .emailVerified(false)
                .build();

        user = userRepository.save(user);

        String otp = otpService.generateOtp(user.getEmail());

        userEventPublisher.publishUserRegistered(
                UserRegisteredEvent.builder()
                        .userId(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .otp(otp)
                        .build()
        );

        return SignupResponse.of(user.getEmail());
    }

    public void verifyOtp(OtpVerifyRequest request) {
        otpService.verifyOtp(request.email(), request.otp());

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException(request.email()));

        user.setEmailVerified(true);
        userRepository.save(user);
    }

    public void resendOtp(OtpResendRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException(request.email()));

        if (user.isEmailVerified()) {
            // Nothing to verify - avoid leaking whether this is intentional misuse
            // vs. a stale client; just refuse quietly with a generic message.
            throw new InvalidOtpException("This account is already verified");
        }

        otpService.assertCanResend(request.email());
        String otp = otpService.generateOtp(user.getEmail());

        userEventPublisher.publishUserRegistered(
                UserRegisteredEvent.builder()
                        .userId(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .otp(otp)
                        .build()
        );
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User precheck = userRepository.findByEmail(request.email()).orElse(null);
        if (precheck != null && precheck.getAuthProvider() == AuthProvider.GOOGLE) {
            throw new InvalidCredentialsException();
            // Deliberately the same generic message as any other bad-credentials
            // case - confirming "this email uses Google sign-in" to an anonymous
            // caller would leak account existence/provider info.
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException();
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException();
        }

        return issueTokens(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        UUID userId = refreshTokenService.validateAndRotate(request.refreshToken());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        return issueTokens(user);
    }

    @Transactional
    public AuthResponse loginWithGoogle(GoogleAuthRequest request) {
        var payload = googleAuthService.verify(request.idToken());

        String email = payload.getEmail();
        Boolean googleEmailVerified = payload.getEmailVerified();
        String name = (String) payload.get("name");

        if (googleEmailVerified == null || !googleEmailVerified) {
            throw new InvalidGoogleTokenException("Google account email is not verified");
        }

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .name(name != null ? name : email)
                    .email(email)
                    .passwordHash(null) // Google users authenticate via Google only, no local password
                    .role(Role.USER)
                    .authProvider(AuthProvider.GOOGLE)
                    .emailVerified(true) // Google already verified this email - no OTP step needed
                    .build();
            return userRepository.save(newUser);
        });

        return issueTokens(user);
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = refreshTokenService.issue(user.getId());
        return AuthResponse.of(accessToken, refreshToken, jwtProperties.getAccessTokenExpiryMs());
    }
}
