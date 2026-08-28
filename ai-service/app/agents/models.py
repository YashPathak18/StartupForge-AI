from pydantic import BaseModel, Field

class Finding(BaseModel):
    title: str = Field(min_length=1, max_length=180)
    detail: str = Field(min_length=1, max_length=2_000)
    confidence: int = Field(ge=1, le=5)

class Competitor(BaseModel):
    name: str
    description: str
    strengths: list[str]
    weaknesses: list[str]
    pricing: str
    url: str | None = None

class CustomerSegment(BaseModel):
    name: str
    description: str
    pain_points: list[str]
    size_estimate: str

class MarketAnalysis(BaseModel):
    industry_overview: str
    market_size: str
    target_market: str
    customer_segments: list[CustomerSegment]
    competitors: list[Competitor]
    market_gaps: list[str]
    opportunities: list[str]
    threats: list[str]
    swot: dict[str, list[str]]  # keys: strengths, weaknesses, opportunities, threats
    key_trends: list[str]
    evidence: list[Finding]

class Feature(BaseModel):
    name: str
    description: str
    priority: str  # must-have, should-have, nice-to-have
    complexity: str  # low, medium, high

class UserStory(BaseModel):
    persona: str
    action: str
    benefit: str
    acceptance_criteria: list[str]

class ProductStrategy(BaseModel):
    problem_statement: str
    product_vision: str
    value_proposition: str
    mvp_scope: str
    features: list[Feature]
    user_stories: list[UserStory]
    roadmap: dict[str, list[str]]  # phase -> features
    success_metrics: list[str]
    evidence: list[Finding]

class ApiEndpoint(BaseModel):
    method: str
    path: str
    description: str

class TechnicalArchitecture(BaseModel):
    system_overview: str
    architecture_pattern: str
    tech_stack: dict[str, str]  # category -> technology
    backend_architecture: str
    frontend_architecture: str
    database_design: str
    api_design: list[ApiEndpoint]
    authentication: str
    security_strategy: str
    deployment_strategy: str
    scalability_plan: str
    evidence: list[Finding]

class Screen(BaseModel):
    name: str
    purpose: str
    key_elements: list[str]
    user_actions: list[str]

class UserFlow(BaseModel):
    name: str
    steps: list[str]
    entry_point: str
    exit_point: str

class UxStrategy(BaseModel):
    personas: list[dict[str, str]]
    user_journeys: list[str]
    user_flows: list[UserFlow]
    information_architecture: str
    sitemap: list[str]
    screens: list[Screen]
    ux_guidelines: list[str]
    accessibility_requirements: list[str]
    responsive_requirements: str
    evidence: list[Finding]

class KPI(BaseModel):
    name: str
    description: str
    target: str
    measurement: str

class BusinessStrategy(BaseModel):
    business_model: str
    revenue_model: str
    pricing_strategy: str
    pricing_tiers: list[dict[str, str]]
    customer_acquisition_strategy: str
    marketing_channels: list[str]
    gtm_strategy: str
    growth_strategy: str
    kpis: list[KPI]
    business_risks: list[str]
    evidence: list[Finding]

class Contradiction(BaseModel):
    domain_a: str
    finding_a: str
    domain_b: str
    finding_b: str
    severity: str  # high, medium, low
    recommendation: str

class DomainScore(BaseModel):
    domain: str
    score: int  # 1-10
    rationale: str

class StrategyAssessment(BaseModel):
    executive_summary: str
    contradictions: list[Contradiction]
    critical_risks: list[str]
    domain_scores: list[DomainScore]
    key_recommendations: list[str]
    overall_viability: str
    next_steps: list[str]
