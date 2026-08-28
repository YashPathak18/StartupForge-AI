"""Chief Strategy Agent.

This agent is the cross-domain evaluation and quality-control layer. It does
not replace the five specialist agents; it evaluates their structured
findings together before blueprint synthesis.
"""

import json

from app.agents.models import StrategyAssessment
from app.llm.service import LlmService
from app.workflow.state import AgentResult, StartupState

llm = LlmService()


STRATEGY_INSTRUCTIONS = """
Act as StartupForge AI's Chief Strategy Agent and strategic quality-control
layer.

Evaluate the five domain outputs together:
1. Market Research & Competitive Intelligence
2. Product Strategy
3. Technical Architecture
4. UI/UX
5. Business Strategy & GTM

Cross-check the domains for contradictions and compatibility.

Explicitly look for issues such as:
- target customer vs pricing mismatch
- customer pain points vs product value proposition mismatch
- MVP scope vs technical architecture over-engineering
- MVP features vs UX primary-flow mismatch
- business model vs product/customer assumptions
- technical choices that do not support the stated MVP

For every contradiction, identify the two conflicting findings, severity,
and a practical recommendation.

Also:
- identify critical risks
- score each domain from 1 to 10 with a rationale
- summarize overall startup viability
- prioritize actionable recommendations
- provide concrete next steps

Do not invent evidence or silently change domain findings. If the supplied
outputs contain insufficient information, state that limitation in the
assessment.

Return the result strictly according to the StrategyAssessment schema.
""".strip()


async def strategy_agent(state: StartupState) -> AgentResult:
    """Evaluate and cross-check all completed specialist outputs."""
    domain_results = {
        name: result
        for name, result in state.get("agent_results", {}).items()
        if name in {"market", "product", "technical", "ux", "business"}
    }

    context = {
        "startupIdea": state["startup_idea"],
        "domainAgentResults": domain_results,
    }

    try:
        assessment = await llm.structured(
            STRATEGY_INSTRUCTIONS,
            json.dumps(context, ensure_ascii=False),
            StrategyAssessment,
        )
    except Exception as exception:
        return {
            "agent": "strategy",
            "status": "failed",
            "findings": {"error": str(exception)},
            "evidence": [],
        }

    if assessment is None:
        return {
            "agent": "strategy",
            "status": "pending",
            "findings": {
                "reason": "OPENAI_API_KEY is not configured"
            },
            "evidence": [],
        }

    return {
        "agent": "strategy",
        "status": "completed",
        "findings": assessment.model_dump(mode="json"),
        "evidence": [],
    }
