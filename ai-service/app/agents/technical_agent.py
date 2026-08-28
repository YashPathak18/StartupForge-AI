"""Technical Architecture Agent."""

from app.agents.agent_runner import run_structured_agent
from app.agents.models import TechnicalArchitecture
from app.workflow.state import AgentResult, StartupState


TECHNICAL_INSTRUCTIONS = """
Act as StartupForge AI's Technical Architecture Agent.

Use the startup idea, product findings, and business context available in the
shared workflow state to design an MVP-suitable technical foundation.

Cover:
- system architecture and service boundaries
- technology stack with justification
- backend and frontend architecture
- database design
- API boundaries
- authentication and authorization
- security strategy
- deployment strategy
- scalability considerations

Prioritize simplicity, maintainability, security, and a realistic MVP.
Avoid unnecessary microservices, infrastructure, or technology choices.
Architecture decisions must support the actual MVP features rather than
inventing requirements.

Return the result strictly according to the TechnicalArchitecture schema.
""".strip()


async def technical_agent(state: StartupState) -> AgentResult:
    """Generate a structured technical architecture."""
    return await run_structured_agent(
        agent_name="technical",
        role_instructions=TECHNICAL_INSTRUCTIONS,
        state=state,
        schema=TechnicalArchitecture,
        research_query=(
            "Technical architecture, technology stack, database design, API "
            "design, authentication, security, deployment, and scalability "
            "for a startup product based on: "
            f"{state['startup_idea']}"
        ),
    )
