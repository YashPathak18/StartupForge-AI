package ai.startupforge.core.blueprint;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public final class BlueprintDtos {

    private BlueprintDtos() {
    }

    public record CreateBlueprintRequest(
            @NotNull UUID projectId,
            UUID generationId,
            @NotBlank String title,
            String executiveSummary,
            String content
    ) {
    }

    public record BlueprintResponse(
            UUID id,
            UUID projectId,
            UUID generationId,
            String title,
            String executiveSummary,
            String content,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }
}
