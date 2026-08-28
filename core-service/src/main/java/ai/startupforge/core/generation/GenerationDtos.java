package ai.startupforge.core.generation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class GenerationDtos {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public record GenerationResponse(
            UUID id,
            UUID projectId,
            GenerationStatus status,
            Object blueprint,
            String errorMessage,
            Instant startedAt,
            Instant completedAt,
            Instant createdAt,
            List<AgentResultResponse> agentResults
    ) {
        public static GenerationResponse from(Generation g, List<AgentResultEntity> results) {
            Object parsedBlueprint = parseJson(g.blueprint);
            List<AgentResultResponse> mappedResults = results == null ? List.of() : 
                    results.stream().map(AgentResultResponse::from).toList();
            
            return new GenerationResponse(
                    g.id,
                    g.projectId,
                    g.status,
                    parsedBlueprint,
                    g.errorMessage,
                    g.startedAt,
                    g.completedAt,
                    g.createdAt,
                    mappedResults
            );
        }
    }

    public record AgentResultResponse(
            UUID id,
            String agentName,
            String status,
            Object result,
            Instant startedAt,
            Instant completedAt
    ) {
        public static AgentResultResponse from(AgentResultEntity e) {
            return new AgentResultResponse(
                    e.id,
                    e.agentName,
                    e.status,
                    parseJson(e.result),
                    e.startedAt,
                    e.completedAt
            );
        }
    }

    private static Object parseJson(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, Object.class);
        } catch (JsonProcessingException e) {
            return json; // Fallback to raw string if invalid JSON
        }
    }
}
