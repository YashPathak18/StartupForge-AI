# API Overview

The backend is organized behind the Spring Cloud Gateway.

## Authentication

```text
/api/v1/auth/**
```

Handled by the Auth Service.

## Projects

```text
/api/v1/projects/**
```

Handled by the Core Service.

## Generations

```text
/api/v1/generations/**
```

Handled by the Core Service.

## Blueprints

```text
/api/v1/blueprints/**
```

Handled by the Core Service.

Examples:

```text
POST /api/v1/blueprints
GET  /api/v1/blueprints/{id}
GET  /api/v1/blueprints/project/{projectId}
GET  /api/v1/blueprints/project/{projectId}/latest
GET  /api/v1/blueprints/{blueprintId}/export
```

## Orchestration

```text
/api/v1/orchestrate/**
```

Routes to the Orchestrator Service, which coordinates the AI service and core application flow.
