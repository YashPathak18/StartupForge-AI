package ai.startupforge.core.generation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface GenerationRepository extends JpaRepository<Generation, UUID> {
    List<Generation> findAllByProjectIdOrderByCreatedAtDesc(UUID projectId);
    Optional<Generation> findTopByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
