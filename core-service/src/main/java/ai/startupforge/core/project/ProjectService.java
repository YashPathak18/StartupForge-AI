package ai.startupforge.core.project;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
class ProjectService {
    private final ProjectRepository projects;
    private final Clock clock;

    ProjectService(ProjectRepository projects) {
        this.projects = projects;
        this.clock = Clock.systemUTC();
    }

    ProjectResponse create(UUID ownerId, CreateProjectRequest request) {
        Instant now = clock.instant();
        Project project = new Project();
        project.id = UUID.randomUUID();
        project.ownerId = ownerId;
        project.name = request.name().trim();
        project.startupIdea = request.startupIdea().trim();
        project.status = ProjectStatus.DRAFT;
        project.createdAt = now;
        project.updatedAt = now;
        return ProjectResponse.from(projects.save(project));
    }

    List<ProjectResponse> list(UUID ownerId) {
        return projects.findAllByOwnerIdOrderByUpdatedAtDesc(ownerId).stream().map(ProjectResponse::from).toList();
    }

    ProjectResponse get(UUID ownerId, UUID projectId) {
        return ProjectResponse.from(findOwned(ownerId, projectId));
    }

    ProjectResponse update(UUID ownerId, UUID projectId, UpdateProjectRequest request) {
        Project project = findOwned(ownerId, projectId);
        project.name = request.name().trim();
        project.startupIdea = request.startupIdea().trim();
        project.updatedAt = clock.instant();
        return ProjectResponse.from(projects.save(project));
    }

    void delete(UUID ownerId, UUID projectId) {
        projects.delete(findOwned(ownerId, projectId));
    }

    private Project findOwned(UUID ownerId, UUID projectId) {
        Project project = projects.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
        if (!project.ownerId.equals(ownerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }
        return project;
    }

    public void updateStatus(UUID projectId, ProjectStatus status) {
        Project project = projects.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
        project.setStatus(status);
        project.setUpdatedAt(clock.instant());
        projects.save(project);
    }
}
