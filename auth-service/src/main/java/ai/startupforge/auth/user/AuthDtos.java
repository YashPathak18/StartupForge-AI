package ai.startupforge.auth.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

record RegisterRequest(@Email @NotBlank String email, @NotBlank @Size(min = 12, max = 128) String password) {
}

record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {
}

record AuthResponse(UUID userId, String email, String accessToken, long expiresInSeconds) {
}
