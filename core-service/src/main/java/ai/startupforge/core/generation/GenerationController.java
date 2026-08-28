package ai.startupforge.core.generation;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/projects/{projectId}/generations")
class GenerationController {
    private final GenerationService generations;

    GenerationController(GenerationService generations) {
        this.generations = generations;
    }

    @PostMapping
    ResponseEntity<GenerationDtos.GenerationResponse> trigger(
            @RequestHeader("X-User-Id") UUID ownerId,
            @PathVariable UUID projectId) {
        GenerationDtos.GenerationResponse response = generations.trigger(ownerId, projectId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    List<GenerationDtos.GenerationResponse> list(
            @RequestHeader("X-User-Id") UUID ownerId,
            @PathVariable UUID projectId) {
        return generations.listByProject(ownerId, projectId);
    }

    @GetMapping("/latest")
    GenerationDtos.GenerationResponse getLatest(
            @RequestHeader("X-User-Id") UUID ownerId,
            @PathVariable UUID projectId) {
        return generations.getLatest(ownerId, projectId);
    }

    @GetMapping("/{generationId}")
    GenerationDtos.GenerationResponse get(
            @RequestHeader("X-User-Id") UUID ownerId,
            @PathVariable UUID projectId,
            @PathVariable UUID generationId) {
        return generations.get(ownerId, generationId);
    }
}
