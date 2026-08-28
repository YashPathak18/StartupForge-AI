"""UI/UX Strategy Agent."""

from app.agents.agent_runner import run_structured_agent
from app.agents.models import UxStrategy
from app.workflow.state import AgentResult, StartupState


UX_INSTRUCTIONS = """
Act as StartupForge AI's UI/UX Strategy Agent.

Use the startup idea, customer/persona findings, product requirements, and
user stories available in the shared workflow state.

Define:
- user personas
- key user journeys
- end-to-end user flows
- information architecture
- sitemap/navigation
- required screens and their purpose
- key screen elements and user actions
- UX guidelines
- accessibility requirements
- responsive-design requirements
- important empty, loading, and error states

Keep the experience practical for the defined MVP. Do not invent major
product capabilities that are absent from the product strategy. Ensure the
primary user flows are consistent with the MVP scope.

Return the result strictly according to the UxStrategy schema.
""".strip()


async def ux_agent(state: StartupState) -> AgentResult:
    """Generate a structured UI/UX strategy."""
    return await run_structured_agent(
        agent_name="ux",
        role_instructions=UX_INSTRUCTIONS,
        state=state,
        schema=UxStrategy,
        research_query=(
            "UX strategy, user personas, user journeys, user flows, "
            "information architecture, screens, accessibility, and "
            "responsive design for: "
            f"{state['startup_idea']}"
        ),
    )
