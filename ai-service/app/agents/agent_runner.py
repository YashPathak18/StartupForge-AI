"""Shared execution helper for StartupForge domain agents.

Domain agents should remain thin: they define the domain-specific research
objective and instructions, while this module handles common LLM execution,
context assembly, structured output conversion, and failure handling.
"""

import json
from typing import Any, TypeVar

from pydantic import BaseModel

from app.llm.service import LlmService
from app.research.service import ResearchService
from app.workflow.state import AgentResult, StartupState

SchemaT = TypeVar("SchemaT", bound=BaseModel)

llm = LlmService()
research = ResearchService()


async def run_structured_agent(
    *,
    agent_name: str,
    role_instructions: str,
    state: StartupState,
    schema: type[SchemaT],
    research_query: str | None = None,
) -> AgentResult:
    """Execute a domain agent and return a normalized AgentResult.

    The agent receives the startup idea plus previously completed findings.
    Research evidence is attached separately so downstream agents and the
    Chief Strategy Agent can distinguish evidence from generated analysis.
    """

    evidence: list[dict[str, str]] = []

    if research_query:
        try:
            evidence = await research.gather(research_query)
        except Exception as exc:
            # Research failure should not prevent the LLM agent from running.
            # The failure is preserved as evidence metadata for observability.
            evidence = [
                {
                    "type": "research_error",
                    "detail": str(exc),
                }
            ]

    completed_findings: dict[str, Any] = {}
    for name, result in state.get("agent_results", {}).items():
        if isinstance(result, dict) and "findings" in result:
            completed_findings[name] = result["findings"]

    context = {
        "startupIdea": state["startup_idea"],
        "completedAgentFindings": completed_findings,
        "evidence": evidence,
    }

    try:
        analysis = await llm.structured(
            role_instructions,
            json.dumps(context, ensure_ascii=False),
            schema,
        )
    except Exception as exc:
        return {
            "agent": agent_name,
            "status": "failed",
            "findings": {"error": str(exc)},
            "evidence": evidence,
        }

    if analysis is None:
        return {
            "agent": agent_name,
            "status": "pending",
            "findings": {
                "reason": "OPENAI_API_KEY is not configured"
            },
            "evidence": evidence,
        }

    return {
        "agent": agent_name,
        "status": "completed",
        "findings": analysis.model_dump(mode="json"),
        "evidence": evidence,
    }
