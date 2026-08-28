"""Shared LangGraph state for StartupForge AI."""

from typing import Any, Literal, TypedDict


AgentName = Literal[
    "market",
    "product",
    "technical",
    "ux",
    "business",
    "strategy",
]


AgentStatus = Literal["pending", "in_progress", "completed", "failed"]


class AgentResult(TypedDict, total=False):
    agent: AgentName
    status: AgentStatus
    findings: dict[str, Any]
    evidence: list[dict[str, Any]]


class StartupState(TypedDict, total=False):
    """State shared by every node in the startup-planning graph."""

    project_id: str
    generation_id: str
    startup_idea: str

    workstreams: list[AgentName]

    agent_results: dict[str, AgentResult]
    agent_status: dict[str, AgentStatus]
    agent_attempts: dict[str, int]
    research_findings: dict[str, list[dict[str, Any]]]

    blueprint: dict[str, Any]
    status: str
