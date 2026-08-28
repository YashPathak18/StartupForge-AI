"""LangGraph workflow nodes for StartupForge AI.

Nodes adapt the specialist agents to LangGraph. Agent-specific reasoning
remains inside the agent modules.
"""

from app.agents.base import record_result
from app.agents.business_agent import business_agent
from app.agents.market_agent import market_agent
from app.agents.product_agent import product_agent
from app.agents.strategy_agent import strategy_agent
from app.agents.technical_agent import technical_agent
from app.agents.ux_agent import ux_agent
from app.workflow.state import StartupState


async def decompose(state: StartupState) -> dict:
    """Initialize the five specialist workstreams."""
    return {
        "workstreams": ["market", "product", "technical", "ux", "business"],
        "agent_results": {},
        "agent_status": {},
        "research_findings": {},
        "status": "in_progress",
    }


async def run_market(state: StartupState) -> dict:
    return await record_result(state, "market", market_agent)


async def run_product(state: StartupState) -> dict:
    return await record_result(state, "product", product_agent)


async def run_technical(state: StartupState) -> dict:
    return await record_result(state, "technical", technical_agent)


async def run_ux(state: StartupState) -> dict:
    return await record_result(state, "ux", ux_agent)


async def run_business(state: StartupState) -> dict:
    return await record_result(state, "business", business_agent)


async def run_strategy(state: StartupState) -> dict:
    """Run the Chief Strategy Agent after specialist outputs are available."""
    return await record_result(state, "strategy", strategy_agent)
