package ai.startupforge.core.blueprint;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BlueprintService {

    private final BlueprintRepository blueprintRepository;

    public BlueprintService(BlueprintRepository blueprintRepository) {
        this.blueprintRepository = blueprintRepository;
    }

    public BlueprintDtos.BlueprintResponse create(
            BlueprintDtos.CreateBlueprintRequest request
    ) {
        Blueprint blueprint = new Blueprint();
        blueprint.setProjectId(request.projectId());
        blueprint.setGenerationId(request.generationId());
        blueprint.setTitle(request.title());
        blueprint.setExecutiveSummary(request.executiveSummary());
        blueprint.setContent(request.content());

        return toResponse(blueprintRepository.save(blueprint));
    }

    @Transactional(readOnly = true)
    public BlueprintDtos.BlueprintResponse get(UUID id) {
        Blueprint blueprint = blueprintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Blueprint not found: " + id
                ));

        return toResponse(blueprint);
    }

    @Transactional(readOnly = true)
    public List<BlueprintDtos.BlueprintResponse> getByProject(UUID projectId) {
        return blueprintRepository
                .findByProjectIdOrderByCreatedAtDesc(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BlueprintDtos.BlueprintResponse getLatest(UUID projectId) {
        return blueprintRepository
                .findTopByProjectIdOrderByCreatedAtDesc(projectId)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No blueprint found for project: " + projectId
                ));
    }

    private BlueprintDtos.BlueprintResponse toResponse(Blueprint blueprint) {
        return new BlueprintDtos.BlueprintResponse(
                blueprint.getId(),
                blueprint.getProjectId(),
                blueprint.getGenerationId(),
                blueprint.getTitle(),
                blueprint.getExecutiveSummary(),
                blueprint.getContent(),
                blueprint.getCreatedAt(),
                blueprint.getUpdatedAt()
        );
    }
}
