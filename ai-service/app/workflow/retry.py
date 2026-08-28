"""Retry helpers for recoverable LangGraph agent failures."""

from collections.abc import Awaitable, Callable

from app.workflow.state import AgentResult, AgentName, StartupState

AgentHandler = Callable[[StartupState], Awaitable[AgentResult]]

MAX_AGENT_ATTEMPTS = 2


def get_attempt_count(state: StartupState, agent_name: AgentName) -> int:
    """Read the current attempt count from workflow state."""
    return int(state.get("agent_attempts", {}).get(agent_name, 0))


def should_retry(state: StartupState, agent_name: AgentName) -> bool:
    """Retry a failed agent only while the attempt budget remains."""
    result = state.get("agent_results", {}).get(agent_name)

    if not result or result.get("status") != "failed":
        return False

    return get_attempt_count(state, agent_name) < MAX_AGENT_ATTEMPTS


async def run_with_retry(
    state: StartupState,
    agent_name: AgentName,
    handler: AgentHandler,
) -> tuple[AgentResult, int]:
    """Run an agent with a small bounded retry budget.

    Returns the final result and the total number of attempts used.
    Retry is intentionally conservative because LLM calls and external
    research calls can be expensive.
    """
    attempts = get_attempt_count(state, agent_name)

    while attempts < MAX_AGENT_ATTEMPTS:
        attempts += 1

        try:
            result = await handler(state)
        except Exception as exc:
            result = {
                "agent": agent_name,
                "status": "failed",
                "findings": {"error": str(exc)},
                "evidence": [],
            }

        if result.get("status") != "failed":
            return result, attempts

    return result, attempts
