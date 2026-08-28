"""Product Strategy Agent."""

from app.agents.agent_runner import run_structured_agent
from app.agents.models import ProductStrategy
from app.workflow.state import AgentResult, StartupState


PRODUCT_INSTRUCTIONS = """
Act as StartupForge AI's Product Strategy Agent.

Use the startup idea and any completed market findings to define:
- the core problem and target user need
- product vision and value proposition
- a realistic MVP scope
- prioritized features
- user stories with acceptance criteria
- a phased product roadmap
- measurable product success metrics

Use earlier market findings when available. Keep the MVP focused and avoid
adding features simply because they are technically possible. Every feature
should have a clear connection to the identified customer problem.

Return the result strictly according to the ProductStrategy schema.
""".strip()


async def product_agent(state: StartupState) -> AgentResult:
    """Generate a structured product strategy."""
    return await run_structured_agent(
        agent_name="product",
        role_instructions=PRODUCT_INSTRUCTIONS,
        state=state,
        schema=ProductStrategy,
        research_query=(
            "Product strategy, customer problems, MVP features, feature "
            "prioritization, user stories, and product roadmap for: "
            f"{state['startup_idea']}"
        ),
    )
