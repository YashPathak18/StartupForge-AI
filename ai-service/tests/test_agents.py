"""Unit tests for the specialist-agent execution layer."""

from unittest.mock import AsyncMock, patch

import pytest

from app.agents.agent_runner import run_structured_agent
from app.agents.models import MarketAnalysis
from app.workflow.state import StartupState


def _state() -> StartupState:
    return {
        "startup_idea": "An AI platform that helps small businesses validate startup ideas.",
        "agent_results": {},
        "agent_status": {},
        "research_findings": {},
    }


@pytest.mark.asyncio
async def test_agent_runner_returns_structured_findings():
    analysis = MarketAnalysis(
        industry_overview="Software",
        market_size="Unknown",
        target_market="Small businesses",
        customer_segments=[],
        competitors=[],
        market_gaps=["Accessible validation"],
        opportunities=["AI-assisted validation"],
        threats=[],
        swot={
            "strengths": ["Automation"],
            "weaknesses": [],
            "opportunities": ["Growing AI adoption"],
            "threats": [],
        },
        key_trends=["AI adoption"],
        evidence=[],
    )

    with (
        patch("app.agents.agent_runner.research.gather", new_callable=AsyncMock) as gather,
        patch("app.agents.agent_runner.llm.structured", new_callable=AsyncMock) as structured,
    ):
        gather.return_value = []
        structured.return_value = analysis

        result = await run_structured_agent(
            agent_name="market",
            role_instructions="Analyze the market.",
            state=_state(),
            schema=MarketAnalysis,
            research_query="startup validation market",
        )

    assert result["agent"] == "market"
    assert result["status"] == "completed"
    assert result["findings"]["target_market"] == "Small businesses"
    structured.assert_awaited_once()
    gather.assert_awaited_once()


@pytest.mark.asyncio
async def test_agent_runner_preserves_research_failure():
    with (
        patch(
            "app.agents.agent_runner.research.gather",
            new_callable=AsyncMock,
            side_effect=RuntimeError("research unavailable"),
        ),
        patch("app.agents.agent_runner.llm.structured", new_callable=AsyncMock) as structured,
    ):
        structured.return_value = None

        result = await run_structured_agent(
            agent_name="market",
            role_instructions="Analyze the market.",
            state=_state(),
            schema=MarketAnalysis,
            research_query="startup validation market",
        )

    assert result["status"] == "pending"
    assert result["evidence"][0]["type"] == "research_error"
    assert "research unavailable" in result["evidence"][0]["detail"]
