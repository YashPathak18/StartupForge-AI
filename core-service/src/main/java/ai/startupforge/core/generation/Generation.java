package ai.startupforge.core.generation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "generations")
class Generation {
    @Id
    UUID id;

    @Column(nullable = false)
    UUID projectId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    GenerationStatus status;

    @Column(columnDefinition = "JSONB")
    String blueprint;

    @Column(columnDefinition = "TEXT")
    String errorMessage;

    Instant startedAt;

    Instant completedAt;

    @Column(nullable = false)
    Instant createdAt;
}
