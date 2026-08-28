# Deployment

## Local Development

The repository provides Docker-based infrastructure and service containers.

Core infrastructure includes:

- PostgreSQL
- Redis
- Kafka
- MinIO

Application services include:

- Gateway
- Auth Service
- Core Service
- Orchestrator Service
- AI Service

## Environment Variables

Secrets and external API credentials should be supplied through environment variables or an ignored `.env` file.

The repository should contain example configuration only; real credentials must never be committed.

## Production Direction

Production deployment can introduce managed PostgreSQL, object storage, message brokers, centralized logging, metrics, tracing, secret management and service-specific scaling.
