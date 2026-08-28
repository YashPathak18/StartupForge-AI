CREATE TABLE generations (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    blueprint JSONB,
    error_message TEXT,
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_generations_project_id ON generations(project_id);

CREATE TABLE agent_results (
    id UUID PRIMARY KEY,
    generation_id UUID NOT NULL REFERENCES generations(id) ON DELETE CASCADE,
    agent_name VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    result JSONB,
    error_message TEXT,
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ
);

CREATE INDEX idx_agent_results_generation_id ON agent_results(generation_id);

CREATE TABLE research_sources (
    id UUID PRIMARY KEY,
    generation_id UUID NOT NULL REFERENCES generations(id) ON DELETE CASCADE,
    agent_name VARCHAR(64) NOT NULL,
    url TEXT NOT NULL,
    title VARCHAR(500),
    content_summary TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_research_sources_generation_id ON research_sources(generation_id);
