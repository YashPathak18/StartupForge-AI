package ai.startupforge.orchestrator.orchestration;

public record OrchestrationResponse(
        String projectId,
        String generationId,
        String status,
        Object result
) {
}
