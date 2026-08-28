package ai.startupforge.auth.user;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
class AuthService {
    private final UserAccountRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwt;

    AuthService(UserAccountRepository users, PasswordEncoder passwordEncoder, JwtService jwt) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwt = jwt;
    }

    AuthResponse register(RegisterRequest request) {
        String email = normalize(request.email());
        if (users.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        Instant now = Instant.now();
        UserAccount user = new UserAccount();
        user.id = UUID.randomUUID();
        user.email = email;
        user.passwordHash = passwordEncoder.encode(request.password());
        user.role = "FOUNDER";
        user.createdAt = now;
        user.updatedAt = now;
        users.save(user);
        return response(user);
    }

    AuthResponse login(LoginRequest request) {
        UserAccount user = users.findByEmail(normalize(request.email()))
                .filter(candidate -> passwordEncoder.matches(request.password(), candidate.passwordHash))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        return response(user);
    }

    private AuthResponse response(UserAccount user) {
        return new AuthResponse(user.id, user.email, jwt.issue(user), jwt.expirySeconds());
    }

    private String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
