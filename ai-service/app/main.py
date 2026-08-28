import uuid
from fastapi import FastAPI, BackgroundTasks, HTTPException

from app.schemas import BlueprintGenerationRequest, BlueprintGenerationResponse
from app.workflow.graph import workflow

app = FastAPI(title="StartupForge AI Service", version="0.1.0")

# In-memory store for MVP
generations = {}


@app.get("/health")
async def health() -> dict[str, str]:
    return {"status": "ok"}


async def run_generation(generation_id: str, request: BlueprintGenerationRequest):
    try:
        state = await workflow.ainvoke({
            "project_id": str(request.project_id),
            "generation_id": generation_id,
            "startup_idea": request.startup_idea,
            "agent_results": {},
            "agent_status": {},
            "research_findings": {}
        })
        generations[generation_id] = state
    except Exception as e:
        generations[generation_id] = {
            "status": "failed",
            "project_id": str(request.project_id),
            "error": str(e)
        }


@app.post("/v1/generations", response_model=BlueprintGenerationResponse)
async def generate_blueprint(request: BlueprintGenerationRequest, background_tasks: BackgroundTasks) -> BlueprintGenerationResponse:
    generation_id = str(uuid.uuid4())
    generations[generation_id] = {
        "status": "queued",
        "project_id": str(request.project_id)
    }
    background_tasks.add_task(run_generation, generation_id, request)
    return BlueprintGenerationResponse(
        project_id=request.project_id,
        generation_id=generation_id,
        status="queued"
    )

@app.get("/v1/generations/{generation_id}/status", response_model=BlueprintGenerationResponse)
async def get_generation_status(generation_id: str) -> BlueprintGenerationResponse:
    state = generations.get(generation_id)
    if not state:
        raise HTTPException(status_code=404, detail="Generation not found")
        
    status = state.get("status", "unknown")
    project_id = state.get("project_id")
    
    agent_results = state.get("agent_results", {})
    strategy_assessment = agent_results.get("strategy", {}).get("findings") if "strategy" in agent_results else None
    
    sources = []
    for domain, findings in state.get("research_findings", {}).items():
        if isinstance(findings, list):
            sources.extend(findings)
            
    return BlueprintGenerationResponse(
        project_id=project_id,
        generation_id=generation_id,
        status=status,
        agent_results=agent_results,
        strategy_assessment=strategy_assessment,
        blueprint=state.get("blueprint"),
        sources=sources
    )
