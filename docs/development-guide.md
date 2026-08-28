# Development Guide

## Repository Structure

```text
frontend/
gateway/
auth-service/
core-service/
orchestrator-service/
ai-service/
common/
infra/
docs/
```

## Backend

The Java services use Maven and Spring Boot.

The AI service uses Python, FastAPI and LangGraph.

## Development Principle

Services are kept separated by responsibility so individual features can be developed without coupling the entire platform into one application.

## Contribution Flow

1. Create a focused feature change.
2. Keep changes inside the owning service.
3. Avoid duplicating shared configuration.
4. Update documentation when architecture changes.
5. Run the relevant service tests before merging when implementing production changes.
