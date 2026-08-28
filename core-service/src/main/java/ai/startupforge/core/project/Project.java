package ai.startupforge.core.project;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "projects")
class Project {
    @Id
    UUID id;

    @Column(nullable = false)
    UUID ownerId;

    @Column(nullable = false, length = 160)
    String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    String startupIdea;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    ProjectStatus status;

    @Column(nullable = false)
    Instant createdAt;

    @Column(nullable = false)
    Instant updatedAt;

    void setStatus(ProjectStatus status) {
        this.status = status;
    }

    void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
