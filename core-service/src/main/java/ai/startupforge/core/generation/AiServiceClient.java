package ai.startupforge.core.generation;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
class AiServiceClient {
    private static final Logger log = LoggerFactory.getLogger(AiServiceClient.class);
    
    private final RestClient restClient;

    AiServiceClient(@Value("${ai-service.url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public Map<String, Object> generate(String startupIdea) {
        try {
            return restClient.post()
                    .uri("/v1/generations")
                    .body(Map.of("startup_idea", startupIdea))
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        } catch (RestClientException e) {
            log.error("Failed to call AI Service for generation", e);
            throw new RuntimeException("AI Service generation failed", e);
        }
    }
}
