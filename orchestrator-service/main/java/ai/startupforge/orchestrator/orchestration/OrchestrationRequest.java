package ai.startupforge.orchestrator.orchestration;

import jakarta.validation.constraints.NotBlank;

public record OrchestrationRequest(
        @NotBlank String projectId,
        @NotBlank String startupIdea
) {
}
