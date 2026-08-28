package ai.startupforge.core.blueprint;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/blueprints")
public class BlueprintController {

    private final BlueprintService blueprintService;

    public BlueprintController(BlueprintService blueprintService) {
        this.blueprintService = blueprintService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BlueprintDtos.BlueprintResponse create(
            @Valid @RequestBody BlueprintDtos.CreateBlueprintRequest request
    ) {
        return blueprintService.create(request);
    }

    @GetMapping("/{id}")
    public BlueprintDtos.BlueprintResponse get(@PathVariable UUID id) {
        return blueprintService.get(id);
    }

    @GetMapping("/project/{projectId}")
    public List<BlueprintDtos.BlueprintResponse> getByProject(
            @PathVariable UUID projectId
    ) {
        return blueprintService.getByProject(projectId);
    }

    @GetMapping("/project/{projectId}/latest")
    public BlueprintDtos.BlueprintResponse getLatest(
            @PathVariable UUID projectId
    ) {
        return blueprintService.getLatest(projectId);
    }
}
