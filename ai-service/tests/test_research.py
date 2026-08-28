"""Unit tests for the external research service."""

from unittest.mock import MagicMock, patch

import pytest

from app.research.service import ResearchService


@pytest.mark.asyncio
async def test_gather_returns_empty_when_tavily_is_not_configured():
    service = ResearchService()

    config = MagicMock(
        tavily_api_key=None,
        firecrawl_api_key=None,
        research_max_sources=5,
    )

    with patch("app.research.service.settings", return_value=config):
        result = await service.gather("startup market")

    assert result == []


@pytest.mark.asyncio
async def test_gather_uses_tavily_when_firecrawl_is_not_configured():
    service = ResearchService()

    config = MagicMock(
        tavily_api_key="tavily-test",
        firecrawl_api_key=None,
        research_max_sources=2,
    )

    tavily_response = {
        "results": [
            {
                "title": "Example source",
                "url": "https://example.com",
                "content": "Evidence from search.",
            }
        ]
    }

    with (
        patch("app.research.service.settings", return_value=config),
        patch("app.research.service.TavilyClient") as tavily_class,
    ):
        tavily_class.return_value.search.return_value = tavily_response

        result = await service.gather("startup market")

    assert len(result) == 1
    assert result[0]["provider"] == "tavily"
    assert result[0]["url"] == "https://example.com"
    assert result[0]["content"] == "Evidence from search."


@pytest.mark.asyncio
async def test_firecrawl_failure_falls_back_to_tavily_source():
    service = ResearchService()

    config = MagicMock(
        tavily_api_key="tavily-test",
        firecrawl_api_key="firecrawl-test",
        research_max_sources=1,
    )

    source = {
        "title": "Example source",
        "url": "https://example.com",
        "content": "Original search evidence.",
    }

    with (
        patch("app.research.service.settings", return_value=config),
        patch("app.research.service.TavilyClient") as tavily_class,
        patch("app.research.service.Firecrawl") as firecrawl_class,
    ):
        tavily_class.return_value.search.return_value = {"results": [source]}
        firecrawl_class.return_value.scrape.side_effect = RuntimeError(
            "scrape unavailable"
        )

        result = await service.gather("startup market")

    assert result == [
        {
            "provider": "tavily",
            "title": "Example source",
            "url": "https://example.com",
            "content": "Original search evidence.",
        }
    ]
