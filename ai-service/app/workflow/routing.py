"""Routing helpers for the StartupForge LangGraph workflow."""

from app.workflow.conditions import specialists_ready, strategy_ready, workflow_failed
from app.workflow.state import StartupState


def route_after_specialists(state: StartupState) -> str:
    """Route specialist execution to strategy, retry/failure, or completion."""
    if workflow_failed(state):
        return "failed"
    if specialists_ready(state):
        return "strategy"
    return "specialists"


def route_after_strategy(state: StartupState) -> str:
    """Route the workflow after Chief Strategy evaluation."""
    if strategy_ready(state):
        return "synthesis"
    return "failed"
