from uuid import UUID

from pydantic import BaseModel, Field


class BlueprintGenerationRequest(BaseModel):
    project_id: UUID
    startup_idea: str = Field(min_length=20, max_length=10_000)


class BlueprintGenerationResponse(BaseModel):
    project_id: UUID
    generation_id: str | None = None
    status: str
    agent_results: dict | None = None
    strategy_assessment: dict | None = None
    blueprint: dict | None = None
    sources: list[dict] | None = None


class UnifiedBlueprint(BaseModel):
    """Canonical founder-facing blueprint assembled from all six agents."""

    startup_idea: str = Field(min_length=1)
    executive_summary: str = Field(min_length=1)
    market: dict
    product: dict
    technical: dict
    ux: dict
    business: dict
    strategy: dict
    key_decisions: list[str] = Field(default_factory=list)
    next_steps: list[str] = Field(default_factory=list)
