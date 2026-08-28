package ai.startupforge.core.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project testProject;
    private Long ownerId = 1L;

    @BeforeEach
    void setUp() {
        testProject = new Project();
        testProject.setId(10L);
        testProject.setOwnerId(ownerId);
        testProject.setName("StartupIdea");
        testProject.setDescription("An AI SaaS");
        testProject.setStatus(ProjectStatus.DRAFT);
    }

    @Test
    void createProject_ReturnsSavedProject() {
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        Project created = projectService.createProject(ownerId, "StartupIdea", "An AI SaaS");

        assertNotNull(created);
        assertEquals("StartupIdea", created.getName());
        assertEquals(ownerId, created.getOwnerId());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void getProject_ValidOwner_ReturnsProject() {
        when(projectRepository.findByIdAndOwnerId(10L, ownerId)).thenReturn(Optional.of(testProject));

        Optional<Project> result = projectService.getProject(10L, ownerId);

        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getId());
    }

    @Test
    void getProject_WrongOwner_ReturnsEmpty() {
        when(projectRepository.findByIdAndOwnerId(10L, 2L)).thenReturn(Optional.empty());

        Optional<Project> result = projectService.getProject(10L, 2L);

        assertFalse(result.isPresent());
    }

    @Test
    void updateProjectStatus_ValidProject_UpdatesSuccessfully() {
        when(projectRepository.findByIdAndOwnerId(10L, ownerId)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        Project updated = projectService.updateProjectStatus(10L, ownerId, ProjectStatus.GENERATING);

        assertNotNull(updated);
        assertEquals(ProjectStatus.GENERATING, updated.getStatus());
    }
}
