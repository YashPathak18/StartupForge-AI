export interface User {
  userId: string;
  email: string;
  role?: string;
  accessToken: string;
  expiresInSeconds: number;
}

export type ProjectStatus = "DRAFT" | "GENERATING" | "READY" | "FAILED";

export interface Project {
  id: string;
  ownerId: string;
  name: string;
  startupIdea: string;
  status: ProjectStatus;
  createdAt: string;
  updatedAt: string;
}

export type GenerationStatus = "PENDING" | "RUNNING" | "COMPLETED" | "FAILED";

export interface Finding {
  claim: string;
  evidence: string;
  source_url?: string;
}

export interface Competitor {
  name: string;
  description: string;
  strengths: string[];
  weaknesses: string[];
  pricing: string;
  url?: string;
}

export interface CustomerSegment {
  name: string;
  description: string;
  pain_points: string[];
  size_estimate: string;
}

export interface MarketAnalysis {
  industry_overview: string;
  market_size: string;
  target_market: string;
  customer_segments: CustomerSegment[];
  competitors: Competitor[];
  market_gaps: string[];
  opportunities: string[];
  threats: string[];
  swot?: {
    strengths?: string[];
    weaknesses?: string[];
    opportunities?: string[];
    threats?: string[];
  };
  key_trends: string[];
  evidence: Finding[];
}

export interface Feature {
  name: string;
  description: string;
  priority: string;
  complexity: string;
}

export interface UserStory {
  persona: string;
  action: string;
  benefit: string;
  acceptance_criteria: string[];
}

export interface ProductStrategy {
  problem_statement: string;
  product_vision: string;
  value_proposition: string;
  mvp_scope: string;
  features: Feature[];
  user_stories: UserStory[];
  roadmap: Record<string, string[]>;
  success_metrics: string[];
  evidence: Finding[];
}

export interface ApiEndpoint {
  method: string;
  path: string;
  description: string;
}

export interface TechnicalArchitecture {
  system_overview: string;
  architecture_pattern: string;
  tech_stack: Record<string, string>;
  backend_architecture: string;
  frontend_architecture: string;
  database_design: string;
  api_design: ApiEndpoint[];
  authentication: string;
  security_strategy: string;
  deployment_strategy: string;
  scalability_plan: string;
  evidence: Finding[];
}

export interface Screen {
  name: string;
  purpose: string;
  key_elements: string[];
  user_actions: string[];
}

export interface UserFlow {
  name: string;
  steps: string[];
  entry_point: string;
  exit_point: string;
}

export interface UxStrategy {
  personas: Array<Record<string, string>>;
  user_journeys: string[];
  user_flows: UserFlow[];
  information_architecture: string;
  sitemap: string[];
  screens: Screen[];
  ux_guidelines: string[];
  accessibility_requirements: string[];
  responsive_requirements: string;
  evidence: Finding[];
}

export interface KPI {
  name: string;
  description: string;
  target: string;
  measurement: string;
}

export interface BusinessStrategy {
  business_model: string;
  revenue_model: string;
  pricing_strategy: string;
  pricing_tiers: Array<Record<string, string>>;
  customer_acquisition_strategy: string;
  marketing_channels: string[];
  gtm_strategy: string;
  growth_strategy: string;
  kpis: KPI[];
  business_risks: string[];
  evidence: Finding[];
}

export interface Contradiction {
  domain_a: string;
  finding_a: string;
  domain_b: string;
  finding_b: string;
  severity: "high" | "medium" | "low" | string;
  recommendation: string;
}

export interface DomainScore {
  domain: string;
  score: number;
  rationale: string;
}

export interface StrategyAssessment {
  executive_summary: string;
  contradictions: Contradiction[];
  critical_risks: string[];
  domain_scores: DomainScore[];
  key_recommendations: string[];
  overall_viability: string;
  next_steps: string[];
}

export interface UnifiedBlueprint {
  startup_idea?: string;
  executive_summary?: string;
  market_analysis?: MarketAnalysis;
  product_strategy?: ProductStrategy;
  technical_architecture?: TechnicalArchitecture;
  ux_strategy?: UxStrategy;
  business_strategy?: BusinessStrategy;
  strategy_assessment?: StrategyAssessment;
  sources?: Array<{ url: string; title?: string; summary?: string }>;
  [key: string]: any;
}

export interface AgentResult {
  id: string;
  agentName: string;
  status: string;
  result: any;
  startedAt?: string;
  completedAt?: string;
}

export interface GenerationResponse {
  id: string;
  projectId: string;
  status: GenerationStatus;
  blueprint: UnifiedBlueprint | null;
  errorMessage?: string | null;
  startedAt?: string | null;
  completedAt?: string | null;
  createdAt: string;
  agentResults?: AgentResult[];
}