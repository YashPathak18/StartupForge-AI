-- Create persistent storage for the unified StartupForge blueprint.
-- This migration belongs to the Core Service Flyway history.

CREATE TABLE IF NOT EXISTS blueprints (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL,
    generation_id UUID,
    title TEXT NOT NULL,
    executive_summary TEXT,
    content TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_blueprints_project_id
    ON blueprints(project_id);

CREATE INDEX IF NOT EXISTS idx_blueprints_generation_id
    ON blueprints(generation_id);

CREATE INDEX IF NOT EXISTS idx_blueprints_created_at
    ON blueprints(created_at DESC);
