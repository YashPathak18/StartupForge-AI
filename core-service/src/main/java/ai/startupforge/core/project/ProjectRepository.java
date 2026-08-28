package ai.startupforge.core.project;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findAllByOwnerIdOrderByUpdatedAtDesc(UUID ownerId);
}
