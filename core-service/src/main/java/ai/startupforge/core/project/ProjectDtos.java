package ai.startupforge.core.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;

record CreateProjectRequest(
        @NotBlank @Size(max = 160) String name,
        @NotBlank @Size(min = 20, max = 10_000) String startupIdea) {
}

record UpdateProjectRequest(
        @NotBlank @Size(max = 160) String name,
        @NotBlank @Size(min = 20, max = 10_000) String startupIdea) {
}

record ProjectResponse(
        UUID id,
        UUID ownerId,
        String name,
        String startupIdea,
        ProjectStatus status,
        Instant createdAt,
        Instant updatedAt) {
    static ProjectResponse from(Project project) {
        return new ProjectResponse(project.id, project.ownerId, project.name, project.startupIdea,
                project.status, project.createdAt, project.updatedAt);
    }
}
