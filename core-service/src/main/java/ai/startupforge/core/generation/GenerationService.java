package ai.startupforge.core.generation;

import ai.startupforge.core.project.ProjectResponse;
import ai.startupforge.core.project.ProjectService;
import ai.startupforge.core.project.ProjectStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
class GenerationService {
    private static final Logger log = LoggerFactory.getLogger(GenerationService.class);
    private final GenerationRepository generations;
    private final AgentResultRepository agentResults;
    private final ProjectService projectService;
    private final AiServiceClient aiServiceClient;
    private final Clock clock;
    private final ObjectMapper mapper;

    GenerationService(GenerationRepository generations, AgentResultRepository agentResults,
                      ProjectService projectService, AiServiceClient aiServiceClient) {
        this.generations = generations;
        this.agentResults = agentResults;
        this.projectService = projectService;
        this.aiServiceClient = aiServiceClient;
        this.clock = Clock.systemUTC();
        this.mapper = new ObjectMapper();
    }

    @Transactional
    public GenerationDtos.GenerationResponse trigger(UUID ownerId, UUID projectId) {
        ProjectResponse project = projectService.get(ownerId, projectId);

        Instant now = clock.instant();
        Generation generation = new Generation();
        generation.id = UUID.randomUUID();
        generation.projectId = projectId;
        generation.status = GenerationStatus.PENDING;
        generation.createdAt = now;
        generation.startedAt = now;
        
        generations.save(generation);
        projectService.updateStatus(projectId, ProjectStatus.GENERATING);

        try {
            Map<String, Object> response = aiServiceClient.generate(project.startupIdea());
            
            generation.status = GenerationStatus.COMPLETED;
            generation.completedAt = clock.instant();
            
            if (response != null && response.containsKey("blueprint")) {
                generation.blueprint = mapper.writeValueAsString(response.get("blueprint"));
            }

            generations.save(generation);
            projectService.updateStatus(projectId, ProjectStatus.READY);

            if (response != null && response.containsKey("agent_results") && response.get("agent_results") instanceof List<?> list) {
                for (Object item : list) {
                    if (item instanceof Map<?, ?> map) {
                        AgentResultEntity entity = new AgentResultEntity();
                        entity.id = UUID.randomUUID();
                        entity.generationId = generation.id;
                        entity.agentName = (String) map.getOrDefault("agent_name", "Unknown");
                        entity.status = (String) map.getOrDefault("status", "COMPLETED");
                        
                        if (map.containsKey("result")) {
                            entity.result = mapper.writeValueAsString(map.get("result"));
                        }
                        
                        entity.startedAt = generation.startedAt;
                        entity.completedAt = generation.completedAt;
                        agentResults.save(entity);
                    }
                }
            }
            
            List<AgentResultEntity> results = agentResults.findAllByGenerationIdOrderByStartedAtAsc(generation.id);
            return GenerationDtos.GenerationResponse.from(generation, results);

        } catch (Exception e) {
            log.error("Generation failed for project {}", projectId, e);
            generation.status = GenerationStatus.FAILED;
            generation.errorMessage = e.getMessage();
            generation.completedAt = clock.instant();
            generations.save(generation);
            projectService.updateStatus(projectId, ProjectStatus.FAILED);
            
            return GenerationDtos.GenerationResponse.from(generation, List.of());
        }
    }

    public GenerationDtos.GenerationResponse getLatest(UUID ownerId, UUID projectId) {
        projectService.get(ownerId, projectId);
        Generation generation = generations.findTopByProjectIdOrderByCreatedAtDesc(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No generations found for project"));
        List<AgentResultEntity> results = agentResults.findAllByGenerationIdOrderByStartedAtAsc(generation.id);
        return GenerationDtos.GenerationResponse.from(generation, results);
    }

    public GenerationDtos.GenerationResponse get(UUID ownerId, UUID generationId) {
        Generation generation = generations.findById(generationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Generation not found"));
        projectService.get(ownerId, generation.projectId);
        List<AgentResultEntity> results = agentResults.findAllByGenerationIdOrderByStartedAtAsc(generation.id);
        return GenerationDtos.GenerationResponse.from(generation, results);
    }

    public List<GenerationDtos.GenerationResponse> listByProject(UUID ownerId, UUID projectId) {
        projectService.get(ownerId, projectId);
        return generations.findAllByProjectIdOrderByCreatedAtDesc(projectId).stream()
                .map(g -> GenerationDtos.GenerationResponse.from(g, agentResults.findAllByGenerationIdOrderByStartedAtAsc(g.id)))
                .toList();
    }
}
