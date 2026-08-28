package ai.startupforge.auth.user;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
class JwtService {
    private final SecretKey signingKey;
    private final long expiryMinutes;

    JwtService(@Value("${startupforge.security.jwt-secret}") String secret, @Value("${startupforge.security.jwt-expiry-minutes}") long expiryMinutes) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiryMinutes = expiryMinutes;
    }

    String issue(UserAccount user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.id.toString())
                .claim("email", user.email)
                .claim("role", user.role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiryMinutes, ChronoUnit.MINUTES)))
                .signWith(signingKey)
                .compact();
    }

    long expirySeconds() {
        return expiryMinutes * 60;
    }
}
