package ai.startupforge.core.generation;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface AgentResultRepository extends JpaRepository<AgentResultEntity, UUID> {
    List<AgentResultEntity> findAllByGenerationIdOrderByStartedAtAsc(UUID generationId);
}
