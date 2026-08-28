"""Final blueprint synthesis for StartupForge AI."""

from app.llm.service import LlmService
from app.schemas import UnifiedBlueprint
from app.workflow.state import StartupState


llm = LlmService()


SYNTHESIS_INSTRUCTIONS = """
Act as StartupForge AI's Lead Blueprint Editor.

Combine the five specialist analyses and the Chief Strategy assessment into
one coherent founder-facing startup blueprint.

Rules:
- Preserve the strongest evidence-backed findings from each domain.
- Preserve important contradictions identified by the Chief Strategy Agent.
- Do not invent market facts, competitors, pricing, technical requirements,
  or customer evidence.
- Keep the product scope consistent with the MVP.
- Keep the technical architecture proportional to the MVP.
- Keep UX flows aligned with the product features.
- Keep business and pricing assumptions aligned with the target customer.
- Use the Chief Strategy recommendations when resolving conflicts.
- Produce concise, actionable executive summary, key decisions, and next
  steps.

Return the result strictly according to the UnifiedBlueprint schema.
""".strip()


async def synthesize_blueprint(state: StartupState) -> dict:
    """Generate the canonical unified blueprint from completed agent outputs."""
    results = state.get("agent_results", {})

    context = {
        "startupIdea": state.get("startup_idea", ""),
        "market": results.get("market", {}).get("findings", {}),
        "product": results.get("product", {}).get("findings", {}),
        "technical": results.get("technical", {}).get("findings", {}),
        "ux": results.get("ux", {}).get("findings", {}),
        "business": results.get("business", {}).get("findings", {}),
        "strategy": results.get("strategy", {}).get("findings", {}),
    }

    import json

    blueprint = await llm.structured(
        SYNTHESIS_INSTRUCTIONS,
        json.dumps(context, ensure_ascii=False),
        UnifiedBlueprint,
    )

    if blueprint is None:
        return {
            "status": "in_progress",
            "blueprint": None,
        }

    return {
        "status": "ready",
        "blueprint": blueprint.model_dump(mode="json"),
    }
