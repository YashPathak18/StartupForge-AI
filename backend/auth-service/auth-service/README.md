# Auth Service

Signup, login, JWT + refresh tokens, email OTP verification, Google Sign-In, RBAC (modeled, not yet enforced — see `INTERVIEW_NOTES.md`).

## Prerequisites

- Java 21
- Maven
- A Postgres database (Neon/Supabase) — `auth_db`
- A Redis instance (Upstash) — OTP storage only
- A RabbitMQ instance (CloudAMQP) — publishes `UserRegistered` events
- A Google OAuth2 Client ID (for `/auth/google`) — client secret not required

## Setup

```bash
cp .env.example .env
# fill in real values for AUTH_DB_*, REDIS_*, RABBITMQ_*, JWT_SECRET, GOOGLE_OAUTH_CLIENT_ID
```

Load the `.env` file into your shell (or your IDE's run config) before starting — Spring Boot does not read `.env` files natively. Options: `export $(cat .env | xargs)` on Linux/macOS, or use a plugin like `spring-dotenv` / your IDE's EnvFile support.

## Run

```bash
mvn spring-boot:run
```

Runs on `http://localhost:8081` (or `$SERVER_PORT`) with the `dev` profile active by default. Flyway runs migrations automatically on startup.

## Endpoints

| Method | Path | Auth required | Notes |
|---|---|---|---|
| POST | `/auth/signup` | No | Creates user (unverified), generates OTP, publishes `UserRegistered` event |
| POST | `/auth/otp/verify` | No | Verifies OTP, marks email verified |
| POST | `/auth/otp/resend` | No | Rate-limited (cooldown + max attempts) |
| POST | `/auth/login` | No | Fails if email not yet verified |
| POST | `/auth/refresh` | No | Rotates refresh token (one-time use) |
| POST | `/auth/google` | No | Verifies Google ID token, finds-or-creates user |
| GET | `/users/me` | Yes (Bearer JWT) | Current user profile |
| PUT | `/users/me` | Yes (Bearer JWT) | Update name |

## Testing OTP flow via Postman (before Notification Service exists)

`otp.log-to-console=true` in dev — after hitting `/auth/signup`, check the application console/logs for a line like:

```
[DEV ONLY] Generated OTP for you@example.com: 483920
```

Use that OTP against `/auth/otp/verify`. **Set `OTP_LOG_TO_CONSOLE=false` once Notification Service (Phase 3) sends real emails.**

## Tests

```bash
mvn test
```

`JwtServiceTest` is a plain unit test (no `@SpringBootTest`) so it runs without live DB/Redis/RabbitMQ credentials — useful for CI or a fresh clone before `.env` is filled in.
