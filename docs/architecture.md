# StartupForge AI — Architecture

## Overview

StartupForge AI is an AI-powered startup co-founder platform that coordinates specialized AI agents to turn a startup idea into a structured startup blueprint.

## High-Level Flow

```text
Next.js Frontend
        |
        v
Spring Cloud Gateway
        |
        +----> Auth Service
        |
        +----> Core Service
        |
        v
Orchestrator Service
        |
        v
FastAPI AI Service
        |
        v
LangGraph Multi-Agent Workflow
        |
        +---- Market Analysis Agent
        +---- Product Planning Agent
        +---- Technical Architecture Agent
        +---- UI/UX Agent
        +---- Business Strategy Agent
        |
        v
Strategy / Integration
        |
        v
Unified Startup Blueprint
```

## Service Responsibilities

- **Frontend:** Founder-facing web application and blueprint visualization.
- **Gateway:** Single entry point for backend APIs and gateway-level security.
- **Auth Service:** Authentication, users, JWT-based security.
- **Core Service:** Projects, generations, blueprints and persistence.
- **Orchestrator Service:** Coordinates communication between application services and the AI workflow.
- **AI Service:** FastAPI-based AI execution layer containing LangGraph orchestration and specialist agents.
- **PostgreSQL/pgvector:** Application and vector persistence.
- **Redis:** Caching and short-lived application state.
- **Kafka:** Asynchronous event-driven communication.
- **MinIO:** S3-compatible artifact storage.
