"""Market Research & Competitive Intelligence Agent."""

from app.agents.agent_runner import run_structured_agent
from app.agents.models import MarketAnalysis
from app.workflow.state import AgentResult, StartupState


MARKET_INSTRUCTIONS = """
Act as StartupForge AI's Market Research & Competitive Intelligence Agent.

Analyze:
- industry and target market
- customer segments and pain points
- direct and indirect competitors
- competitor positioning and pricing
- market gaps and opportunities
- key industry trends
- SWOT factors

Use the supplied research evidence to ground factual claims. Clearly
distinguish evidence-backed findings from strategic inference. Do not invent
market-size figures, competitor pricing, sources, or customer facts.

Return the result strictly according to the MarketAnalysis schema.
""".strip()


async def market_agent(state: StartupState) -> AgentResult:
    """Generate a structured market and competitive analysis."""
    return await run_structured_agent(
        agent_name="market",
        role_instructions=MARKET_INSTRUCTIONS,
        state=state,
        schema=MarketAnalysis,
        research_query=(
            "Market size, target customers, competitors, competitor pricing, "
            "customer pain points, market gaps, and industry trends for: "
            f"{state['startup_idea']}"
        ),
    )
