package ai.startupforge.orchestrator.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class AiServiceClient {

    private final RestClient client;

    public AiServiceClient(@Qualifier("aiServiceClient") RestClient client) {
        this.client = client;
    }

    public Object generate(String startupIdea, String projectId, String generationId) {
        return client.post()
                .uri("/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "startup_idea", startupIdea,
                        "project_id", projectId,
                        "generation_id", generationId
                ))
                .retrieve()
                .body(Object.class);
    }
}
