"""Business Strategy & Go-To-Market Agent."""

from app.agents.agent_runner import run_structured_agent
from app.agents.models import BusinessStrategy
from app.workflow.state import AgentResult, StartupState


BUSINESS_INSTRUCTIONS = """
Act as StartupForge AI's Business Strategy & Go-To-Market Agent.

Use the startup idea plus available market, product, technical, and UX
findings from shared workflow state.

Define:
- business model
- revenue model
- pricing strategy and pricing tiers
- customer acquisition strategy
- marketing channels
- go-to-market strategy
- growth strategy
- measurable KPIs
- business risks

Pricing and monetization must be consistent with the target customer and
value proposition. Prefer realistic assumptions and explicitly ground
research-dependent claims in supplied evidence. Do not invent market facts,
customer willingness-to-pay, or competitor pricing.

Return the result strictly according to the BusinessStrategy schema.
""".strip()


async def business_agent(state: StartupState) -> AgentResult:
    """Generate a structured business and GTM strategy."""
    return await run_structured_agent(
        agent_name="business",
        role_instructions=BUSINESS_INSTRUCTIONS,
        state=state,
        schema=BusinessStrategy,
        research_query=(
            "Business model, revenue model, pricing, customer acquisition, "
            "go-to-market, marketing channels, growth strategy, and business "
            "risks for: "
            f"{state['startup_idea']}"
        ),
    )
