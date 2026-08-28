"""External research service used by StartupForge AI agents."""

import asyncio
from typing import Any

from firecrawl import Firecrawl
from tavily import TavilyClient

from app.config import settings


class ResearchService:
    """Collect web evidence through Tavily and optionally enrich it with Firecrawl."""

    async def gather(self, query: str) -> list[dict[str, str]]:
        config = settings()

        if not config.tavily_api_key:
            return []

        tavily = TavilyClient(api_key=config.tavily_api_key)

        try:
            response: dict[str, Any] = await asyncio.to_thread(
                tavily.search,
                query=query,
                search_depth="advanced",
                max_results=config.research_max_sources,
            )
        except Exception:
            return []

        sources = [
            source
            for source in response.get("results", [])
            if isinstance(source, dict) and source.get("url")
        ]

        if not sources:
            return []

        if not config.firecrawl_api_key:
            return [self._from_search(source) for source in sources]

        firecrawl = Firecrawl(api_key=config.firecrawl_api_key)
        results = await asyncio.gather(
            *(self._extract(firecrawl, source) for source in sources)
        )

        return [result for result in results if result is not None]

    async def _extract(
        self,
        firecrawl: Firecrawl,
        source: dict[str, Any],
    ) -> dict[str, str] | None:
        """Enrich one source without aborting the whole research request."""
        url = source.get("url")
        if not url:
            return None

        try:
            scraped = await asyncio.to_thread(
                firecrawl.scrape,
                url,
                formats=["markdown"],
            )
            markdown = (
                getattr(scraped, "markdown", None)
                or source.get("content", "")
            )

            return {
                "provider": "firecrawl",
                "title": source.get("title", "Untitled"),
                "url": url,
                "content": markdown,
            }
        except Exception:
            # Preserve Tavily evidence when Firecrawl cannot scrape a source.
            return self._from_search(source)

    @staticmethod
    def _from_search(source: dict[str, Any]) -> dict[str, str]:
        return {
            "provider": "tavily",
            "title": source.get("title", "Untitled"),
            "url": source.get("url", ""),
            "content": source.get("content", ""),
        }
