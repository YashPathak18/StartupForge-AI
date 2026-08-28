"""Workflow conditions for StartupForge AI."""

from app.workflow.state import StartupState


SPECIALIST_AGENTS = ("market", "product", "technical", "ux", "business")


def specialists_ready(state: StartupState) -> bool:
    """Return True when all five specialist agents completed successfully."""
    results = state.get("agent_results", {})
    return all(
        results.get(agent, {}).get("status") == "completed"
        for agent in SPECIALIST_AGENTS
    )


def workflow_failed(state: StartupState) -> bool:
    """Return True when any specialist or strategy agent has failed."""
    return any(
        result.get("status") == "failed"
        for result in state.get("agent_results", {}).values()
    )


def strategy_ready(state: StartupState) -> bool:
    """Return True when the Chief Strategy Agent completed successfully."""
    return state.get("agent_results", {}).get("strategy", {}).get("status") == "completed"
