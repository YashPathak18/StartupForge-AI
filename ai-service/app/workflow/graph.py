"""Compiled LangGraph workflow for StartupForge AI."""

import json

from pydantic import BaseModel
from langgraph.graph import END, START, StateGraph

from app.agents.base import record_result
from app.agents.strategy_agent import strategy_agent
from app.llm.service import LlmService
from app.workflow.nodes import (
    decompose,
    run_business,
    run_market,
    run_product,
    run_strategy,
    run_technical,
    run_ux,
)
from app.workflow.state import StartupState


llm = LlmService()


class BlueprintNarrative(BaseModel):
    executive_summary: str
    vision: str
    key_findings: list[str]
    blueprint_narrative: str


async def synthesize(state: StartupState) -> dict:
    """Create the final founder-facing blueprint from all agent outputs."""
    statuses = list(state.get("agent_status", {}).values())

    if "failed" in statuses:
        status = "failed"
    elif len(statuses) >= 6 and all(value == "completed" for value in statuses):
        status = "ready"
    else:
        status = "in_progress"

    blueprint = {"startupIdea": state.get("startup_idea")}

    context = {
        "startupIdea": state.get("startup_idea"),
        "strategy": state.get("agent_results", {}).get("strategy", {}).get("findings", {}),
        "market": state.get("agent_results", {}).get("market", {}).get("findings", {}),
        "product": state.get("agent_results", {}).get("product", {}).get("findings", {}),
        "technical": state.get("agent_results", {}).get("technical", {}).get("findings", {}),
        "ux": state.get("agent_results", {}).get("ux", {}).get("findings", {}),
        "business": state.get("agent_results", {}).get("business", {}).get("findings", {}),
    }

    instructions = (
        "Act as the Lead Editor for StartupForge AI. Create a unified, "
        "cohesive final startup blueprint from the strategy assessment and "
        "five domain-agent findings. Preserve important contradictions and "
        "recommendations identified by the Chief Strategy Agent. Do not "
        "invent unsupported facts. Return structured JSON."
    )

    try:
        narrative = await llm.structured(
            instructions,
            json.dumps(context, ensure_ascii=False),
            BlueprintNarrative,
        )
        if narrative:
            blueprint["narrative"] = narrative.model_dump(mode="json")
    except Exception as exc:
        blueprint["narrative_error"] = str(exc)

    return {"status": status, "blueprint": blueprint}


builder = StateGraph(StartupState)

builder.add_node("decompose", decompose)
builder.add_node("market", run_market)
builder.add_node("product", run_product)
builder.add_node("technical", run_technical)
builder.add_node("ux", run_ux)
builder.add_node("business", run_business)
builder.add_node("strategy", run_strategy)
builder.add_node("synthesize", synthesize)

builder.add_edge(START, "decompose")
builder.add_edge("decompose", "market")

# Market research is the first specialist stage. The remaining specialists
# are released after market context has been collected.
builder.add_edge("market", "product")
builder.add_edge("market", "technical")
builder.add_edge("market", "ux")
builder.add_edge("market", "business")

# LangGraph waits for all incoming edges before entering strategy.
builder.add_edge(["product", "technical", "ux", "business"], "strategy")

builder.add_edge("strategy", "synthesize")
builder.add_edge("synthesize", END)

workflow = builder.compile()
