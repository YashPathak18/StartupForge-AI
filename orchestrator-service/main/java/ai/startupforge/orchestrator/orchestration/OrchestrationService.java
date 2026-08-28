package ai.startupforge.orchestrator.orchestration;

import ai.startupforge.orchestrator.client.AiServiceClient;
import ai.startupforge.orchestrator.client.CoreServiceClient;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrchestrationService {

    private final AiServiceClient aiServiceClient;
    private final CoreServiceClient coreServiceClient;

    public OrchestrationService(
            AiServiceClient aiServiceClient,
            CoreServiceClient coreServiceClient) {
        this.aiServiceClient = aiServiceClient;
        this.coreServiceClient = coreServiceClient;
    }

    public OrchestrationResponse startGeneration(OrchestrationRequest request) {
        String generationId = UUID.randomUUID().toString();

        coreServiceClient.createGeneration(
                request.projectId(),
                generationId
        );

        Object result = aiServiceClient.generate(
                request.startupIdea(),
                request.projectId(),
                generationId
        );

        return new OrchestrationResponse(
                request.projectId(),
                generationId,
                "IN_PROGRESS",
                result
        );
    }
}
