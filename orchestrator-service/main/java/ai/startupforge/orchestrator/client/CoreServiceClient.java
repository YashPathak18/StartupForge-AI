package ai.startupforge.orchestrator.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class CoreServiceClient {

    private final RestClient client;

    public CoreServiceClient(@Qualifier("coreServiceClient") RestClient client) {
        this.client = client;
    }

    public void createGeneration(String projectId, String generationId) {
        client.post()
                .uri("/generation")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "projectId", projectId,
                        "generationId", generationId,
                        "status", "IN_PROGRESS"
                ))
                .retrieve()
                .toBodilessEntity();
    }
}
