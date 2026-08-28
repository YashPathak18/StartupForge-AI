package ai.startupforge.orchestrator.orchestration;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orchestration")
public class OrchestrationController {

    private final OrchestrationService orchestrationService;

    public OrchestrationController(OrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @PostMapping("/generate")
    public ResponseEntity<OrchestrationResponse> generate(
            @Valid @RequestBody OrchestrationRequest request) {
        return ResponseEntity.accepted()
                .body(orchestrationService.startGeneration(request));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("orchestrator-service is running");
    }
}
