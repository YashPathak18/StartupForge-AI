"""Shared agent result handling for the LangGraph workflow."""

from collections.abc import Awaitable, Callable

from app.workflow.retry import run_with_retry
from app.workflow.state import AgentName, AgentResult, StartupState


AgentHandler = Callable[[StartupState], Awaitable[AgentResult]]


async def record_result(
    state: StartupState,
    agent_name: AgentName,
    handler: AgentHandler,
) -> dict:
    """Execute an agent, apply the retry budget, and persist its result."""
    result, attempts = await run_with_retry(state, agent_name, handler)

    results = dict(state.get("agent_results", {}))
    agent_status = dict(state.get("agent_status", {}))
    research_findings = dict(state.get("research_findings", {}))
    agent_attempts = dict(state.get("agent_attempts", {}))

    results[agent_name] = result
    agent_status[agent_name] = result.get("status", "failed")
    agent_attempts[agent_name] = attempts

    if result.get("evidence"):
        research_findings[agent_name] = result["evidence"]

    return {
        "agent_results": results,
        "agent_status": agent_status,
        "research_findings": research_findings,
        "agent_attempts": agent_attempts,
    }
