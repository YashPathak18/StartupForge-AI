# StartupForge AI

Turns a startup idea into a validated blueprint (PRD, architecture, roadmap, validation scores) through a multi-agent AI pipeline, backed by a Java microservices backend.

Full architecture, tech stack, and phase-by-phase roadmap: see the project roadmap doc (not included in this repo push — tracked separately).

## Repo structure

```
startupforge/
├── auth-service/          # Signup/login, JWT, refresh tokens, email OTP, Google Sign-In (Phase 1)
├── core-service/          # Projects, reports, generation, chat, exports (Phase 2+)
├── notification-service/  # RabbitMQ consumer, transactional email (Phase 3+)
├── ai-service/             # Python FastAPI + LangGraph multi-agent pipeline (Phase 4+)
├── gateway/                # Spring Cloud Gateway, routes to auth-service + core-service (Phase 2+)
├── frontend/               # Next.js app (Phase 5+)
└── INTERVIEW_NOTES.md      # Running build log: what was built, why, tradeoffs, interview framing
```

Each service folder is independently runnable with its own `pom.xml` (or `requirements.txt` for `ai-service`), `application.yml`, and `.env.example`. No shared/common library folder by design — each folder can be pushed and reviewed in isolation.

## Status

- ✅ **Phase 1 — Auth Service**: built
- ⬜ Phase 2 — Gateway + Core Service CRUD
- ⬜ Phase 3 — RabbitMQ + Notification Service
- ⬜ Phase 4 — Core Service: Real AI Call + Schema Lock
- ⬜ Phase 5 — Frontend
- ⬜ Phase 6 — Chat, Export, Scaffold Zip, Share Link
- ⬜ Phase 7 — Real Multi-Agent Pipeline + Chief Strategy Agent
- ⬜ Phase 8 — Async & Resilience
- ⬜ Phase 9 — RAG
- ⬜ Phase 10 — Deploy & Wrap-Up

See `INTERVIEW_NOTES.md` for the detailed build log per phase.
