package ai.startupforge.core.generation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "agent_results")
class AgentResultEntity {
    @Id
    UUID id;

    @Column(nullable = false)
    UUID generationId;

    @Column(nullable = false, length = 64)
    String agentName;

    @Column(nullable = false, length = 32)
    String status;

    @Column(columnDefinition = "JSONB")
    String result;

    @Column(columnDefinition = "TEXT")
    String errorMessage;

    Instant startedAt;

    Instant completedAt;
}
