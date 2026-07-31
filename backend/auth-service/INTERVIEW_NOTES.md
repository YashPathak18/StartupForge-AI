# StartupForge AI — Interview Notes

Running log of what was built, why, and the tradeoffs accepted — one Build Note per major feature/phase.

---

## Phase 1 — Auth Service

### What was built
A standalone Spring Boot 3 / Java 21 Auth Service with its own `auth_db`:
- Signup with bcrypt password hashing, email OTP verification (Redis-backed, TTL + resend cooldown + max-attempt lockout)
- Login (rejects until email is verified), refresh-token rotation (opaque, DB-stored, one-time-use)
- Google Sign-In via ID-token verification (not the redirect-based OAuth2 login flow)
- `UserRegistered` event published to RabbitMQ on signup/resend (consumed later by Notification Service in Phase 3)
- `GET/PUT /users/me` profile endpoints
- Its own JWT validation filter (`JwtAuthFilter`), independent of the Gateway that arrives in Phase 2

### Problem solved
A user needs to be able to sign up, prove ownership of their email, and log in — with the resulting JWT usable by every other service in the system — before anything else in the roadmap (projects, AI generation, chat) can exist.

### Why this approach over the obvious alternative
- **Per-service JWT validation, not gateway-only** — even though the Gateway (Phase 2) centralizes JWT validation for routing, Auth Service keeps its own filter. The obvious alternative (trust a header the Gateway injects) would leave `/users/me` completely unauthenticated during Phase 1, since the Gateway doesn't exist yet and Phase 1's own exit criteria requires a standalone Postman test. Real systems generally don't trust a single hop's validation as the *only* line of defense either.
- **Opaque, DB-stored refresh tokens instead of a second JWT** — refresh tokens need to be revocable on demand (logout, compromise). A JWT refresh token can't be invalidated without a blocklist, which just reinvents a DB table anyway — so we store it directly and rotate it (one-time use) on every refresh.
- **OTP in Redis, refresh tokens in Postgres** — different durability requirements. OTPs are short-lived, high-write, disposable — a cache is the right tool. Refresh tokens need to survive a Redis restart and be queryable/revocable — a cache is the wrong tool for that.
- **Google Sign-In via ID-token verification, not Spring's OAuth2 redirect login** — the redirect flow assumes a server-rendered session-based app. This is a stateless JWT API behind a separate Next.js frontend; the frontend does Google Sign-In itself and hands us a signed ID token to verify. Simpler, and it's the standard pattern for SPA + API architectures.
- **RBAC modeled but not enforced** — `Role` is a real enum, carried as a JWT claim, but no `@PreAuthorize` gate exists anywhere. Nothing in the 88-day roadmap has an admin-only operation. Building enforcement now would be securing a door that doesn't exist yet; adding the gate later (if ever needed) is a one-line annotation, not new infrastructure.

### Tradeoff accepted
- OTPs are logged to the console in dev (`otp.log-to-console=true`), gated by config, because Notification Service (which actually emails them) doesn't exist until Phase 3. This is temporary scaffolding, not a security decision — it must be flipped off once real email delivery exists.
- Minor duplication across services is accepted by design (per the mono-repo convention) — no shared/common library folder, so every service folder stays independently push-able.

### Likely interviewer follow-ups
- *"Why not just trust the Gateway's JWT validation and skip it in each service?"* → Defense-in-depth; also Phase 1 had to be independently testable before the Gateway existed.
- *"Why opaque refresh tokens instead of a second JWT?"* → Revocability without a blocklist.
- *"How do you know the Google ID token wasn't forged?"* → `GoogleIdTokenVerifier` checks the signature against Google's published public keys and validates the audience claims against our client ID.
- *"What happens if someone signs up with Google using an email that already has a local password account?"* → Currently finds-or-creates by email, meaning a Google sign-in would attach to the existing local account. Worth flagging as an edge case to revisit if it matters for your demo.

### Connection to later phases
- The `UserRegisteredEvent` payload (including OTP) is the exact contract Notification Service (Phase 3) will consume — no changes needed there.
- The Gateway (Phase 2) will front this service's public routes; the `PUBLIC_ROUTES` list in `SecurityConfig` is the same set the Gateway needs to mirror as unauthenticated.
- Role claim in the JWT is what Core Service (and later, if ever needed, an admin gate) would read — already flowing through every issued token.

### Addendum — corrected to Spring Boot 4.1
Initially built against Spring Boot 3.3.4; corrected to match the locked stack decision (Spring Boot 4.1, Java 21) once flagged. Two real breaking changes surfaced from Boot 4's shift to Jackson 3 as the default JSON library:
- **`jjwt-jackson` → `jjwt-gson`**: jjwt 0.12.x's Jackson module pulls in Jackson 2.x internally, which conflicts with Boot 4's Jackson 3 baseline. Gson sidesteps the conflict entirely since JWT (de)serialization is fully internal to jjwt and never touches the app's own JSON stack.
- **`Jackson2JsonMessageConverter` → `JacksonJsonMessageConverter`** in `RabbitMQConfig`: the former is deprecated-for-removal in Spring AMQP 4.x in favor of the Jackson-3-based converter. Matters for Phase 3 too — Notification Service's consumer-side converter needs to match (Jackson 3), or the two services won't agree on how to deserialize `UserRegisteredEvent` off the wire.
- `SecurityConfig` needed no changes — it already used the modern lambda DSL (`authorizeHttpRequests`, `requestMatchers`, `csrf.disable()`) rather than the removed `WebSecurityConfigurerAdapter`/`antMatchers()`, which is what Security 7 (bundled with Boot 4) requires anyway.

**Likely interviewer follow-up:** *"How do you approach a major-version framework upgrade?"* → Identify what the new major version's BOM forces (here: Jackson 3 as default), trace every dependency that assumes the old default (jjwt, Spring AMQP's converter), and fix at the boundary rather than patching symptoms — plus confirm anything consuming the same wire format (Notification Service's queue consumer) gets updated in lockstep.

