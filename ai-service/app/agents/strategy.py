import json

from app.agents.models import StrategyAssessment
from app.llm.service import LlmService
from app.workflow.state import AgentResult, StartupState

llm = LlmService()


async def strategy_agent(state: StartupState) -> AgentResult:
    instructions = (
        "Act as the Chief Strategy Agent. Provide a comprehensive StrategyAssessment of the startup idea based on the provided domain analyses. "
        "Explicitly cross-compare the domain outputs to identify contradictions (e.g. market↔pricing, MVP↔architecture, product↔UX). "
        "Score each domain, identify critical risks, write an executive summary, and provide concrete next steps. "
        "Do not invent evidence. Use only the provided domain findings."
    )
    context = {"startupIdea": state["startup_idea"], "agentResults": state.get("agent_results", {})}
    try:
        assessment = await llm.structured(instructions, json.dumps(context), StrategyAssessment)
    except Exception as exception:
        return {"agent": "strategy", "status": "failed", "findings": {"error": str(exception)}, "evidence": []}
    if assessment is None:
        return {"agent": "strategy", "status": "pending", "findings": {"reason": "OPENAI_API_KEY is not configured"}, "evidence": []}
    return {"agent": "strategy", "status": "completed", "findings": assessment.model_dump(mode="json"), "evidence": []}
