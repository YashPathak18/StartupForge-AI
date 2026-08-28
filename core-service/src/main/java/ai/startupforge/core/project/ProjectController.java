package ai.startupforge.core.project;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects")
class ProjectController {
    private static final String OWNER_HEADER = "X-User-Id";
    private final ProjectService projects;

    ProjectController(ProjectService projects) {
        this.projects = projects;
    }

    @PostMapping
    ResponseEntity<ProjectResponse> create(
            @RequestHeader(OWNER_HEADER) UUID ownerId,
            @Valid @RequestBody CreateProjectRequest request) {
        ProjectResponse project = projects.create(ownerId, request);
        return ResponseEntity.created(URI.create("/projects/" + project.id())).body(project);
    }

    @GetMapping
    List<ProjectResponse> list(@RequestHeader(OWNER_HEADER) UUID ownerId) {
        return projects.list(ownerId);
    }

    @GetMapping("/{projectId}")
    ProjectResponse get(@RequestHeader(OWNER_HEADER) UUID ownerId, @PathVariable UUID projectId) {
        return projects.get(ownerId, projectId);
    }

    @PutMapping("/{projectId}")
    ProjectResponse update(
            @RequestHeader(OWNER_HEADER) UUID ownerId,
            @PathVariable UUID projectId,
            @Valid @RequestBody UpdateProjectRequest request) {
        return projects.update(ownerId, projectId, request);
    }

    @DeleteMapping("/{projectId}")
    ResponseEntity<Void> delete(@RequestHeader(OWNER_HEADER) UUID ownerId, @PathVariable UUID projectId) {
        projects.delete(ownerId, projectId);
        return ResponseEntity.noContent().build();
    }
}
