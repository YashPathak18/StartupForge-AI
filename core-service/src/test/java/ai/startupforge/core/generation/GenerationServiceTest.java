package ai.startupforge.core.generation;

import ai.startupforge.core.project.Project;
import ai.startupforge.core.project.ProjectService;
import ai.startupforge.core.project.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenerationServiceTest {

    @Mock
    private GenerationRepository generationRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private AiServiceClient aiServiceClient;

    @InjectMocks
    private GenerationService generationService;

    private Project testProject;
    private Generation testGeneration;
    private Long ownerId = 1L;
    private Long projectId = 10L;

    @BeforeEach
    void setUp() {
        testProject = new Project();
        testProject.setId(projectId);
        testProject.setOwnerId(ownerId);
        testProject.setStatus(ProjectStatus.DRAFT);

        testGeneration = new Generation();
        testGeneration.setId(100L);
        testGeneration.setProjectId(projectId);
        testGeneration.setStatus(GenerationStatus.PENDING);
    }

    @Test
    void triggerGeneration_ValidProject_StartsGeneration() {
        when(projectService.getProject(projectId, ownerId)).thenReturn(Optional.of(testProject));
        when(generationRepository.save(any(Generation.class))).thenReturn(testGeneration);
        
        // Mock async call implicitly or handle doNothing for async execution if it's spy

        Generation result = generationService.triggerGeneration(projectId, ownerId, "Generate a tech stack");

        assertNotNull(result);
        assertEquals(GenerationStatus.PENDING, result.getStatus());
        verify(projectService, times(1)).updateProjectStatus(projectId, ownerId, ProjectStatus.GENERATING);
        verify(generationRepository, times(1)).save(any(Generation.class));
    }

    @Test
    void triggerGeneration_InvalidProject_ThrowsException() {
        when(projectService.getProject(projectId, ownerId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> generationService.triggerGeneration(projectId, ownerId, "prompt"));
    }
}
