import json

from app.agents.models import (
    MarketAnalysis,
    ProductStrategy,
    TechnicalArchitecture,
    UxStrategy,
    BusinessStrategy,
)
from app.llm.service import LlmService
from app.research.service import ResearchService
from app.workflow.state import AgentResult, StartupState

research = ResearchService()
llm = LlmService()


async def market_agent(state: StartupState) -> AgentResult:
    evidence = await research.gather(f"Market, competitors, pricing, and customer pain points for: {state['startup_idea']}")
    return await analyse("market", "Analyze the market, competitors, target segments, pricing, and opportunities. Ground claims in the supplied evidence.", state, MarketAnalysis, evidence)


async def product_agent(state: StartupState) -> AgentResult:
    evidence = await research.gather(f"Product strategy, features, and MVP scope for: {state['startup_idea']}")
    return await analyse("product", "Act as a product strategist. Define the problem, value proposition, MVP scope, prioritized features, user stories, and roadmap.", state, ProductStrategy, evidence)


async def technical_agent(state: StartupState) -> AgentResult:
    evidence = await research.gather(f"Technical architecture, stack, and deployment for: {state['startup_idea']}")
    return await analyse("technical", "Act as a technical architect. Propose an MVP-suitable architecture, technology choices, data model, API boundaries, security, and deployment path. Avoid over-engineering.", state, TechnicalArchitecture, evidence)


async def ux_agent(state: StartupState) -> AgentResult:
    evidence = await research.gather(f"UX strategy, personas, and user journeys for: {state['startup_idea']}")
    return await analyse("ux", "Act as a UX strategist. Define personas, user journeys, information architecture, key screens, accessibility needs, and error states.", state, UxStrategy, evidence)


async def business_agent(state: StartupState) -> AgentResult:
    evidence = await research.gather(f"Business model, pricing, and go-to-market strategy for: {state['startup_idea']}")
    return await analyse("business", "Act as a business and go-to-market strategist. Define the business model, pricing approach, acquisition channels, KPIs, and business risks.", state, BusinessStrategy, evidence)


async def analyse(agent: str, role_instructions: str, state: StartupState, schema: type, evidence: list[dict[str, str]] | None = None) -> AgentResult:
    context = {
        "startupIdea": state["startup_idea"],
        "completedAgentFindings": {name: result["findings"] for name, result in state.get("agent_results", {}).items()},
        "evidence": evidence or [],
    }
    try:
        analysis = await llm.structured(role_instructions, json.dumps(context), schema)
    except Exception as exception:
        return {"agent": agent, "status": "failed", "findings": {"error": str(exception)}, "evidence": evidence or []}
    if analysis is None:
        return {"agent": agent, "status": "pending", "findings": {"reason": "OPENAI_API_KEY is not configured"}, "evidence": evidence or []}
    return {"agent": agent, "status": "completed", "findings": analysis.model_dump(mode="json"), "evidence": evidence or []}
