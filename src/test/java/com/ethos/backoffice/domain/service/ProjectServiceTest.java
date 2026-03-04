package com.ethos.backoffice.domain.service;

import com.ethos.backoffice.domain.model.Project;
import com.ethos.backoffice.domain.model.ProjectStatus;
import com.ethos.backoffice.domain.port.out.ProjectRepositoryPort;
import com.ethos.backoffice.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepositoryPort projectRepositoryPort;

    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        projectService = new ProjectService(projectRepositoryPort);
    }

    @Test
    void createProject_ShouldReturnSavedProject() {
        Project project = buildProject();
        when(projectRepositoryPort.save(project)).thenReturn(project);

        Project result = projectService.createProject(project);

        assertThat(result).isEqualTo(project);
        verify(projectRepositoryPort).save(project);
    }

    @Test
    void getProjectById_ShouldReturnProject_WhenExists() {
        UUID id = UUID.randomUUID();
        Project project = buildProject(id);
        when(projectRepositoryPort.findById(id)).thenReturn(Optional.of(project));

        Project result = projectService.getProjectById(id);

        assertThat(result).isEqualTo(project);
    }

    @Test
    void getProjectById_ShouldThrowResourceNotFoundException_WhenNotExists() {
        UUID id = UUID.randomUUID();
        when(projectRepositoryPort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getProjectById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAllProjects_ShouldReturnPagedList() {
        List<Project> projects = List.of(buildProject(), buildProject());
        when(projectRepositoryPort.findAll(0, 10)).thenReturn(projects);

        List<Project> result = projectService.getAllProjects(0, 10);

        assertThat(result).hasSize(2);
    }

    @Test
    void updateProject_ShouldUpdateFields_WhenProjectExists() {
        UUID id = UUID.randomUUID();
        Project existing = buildProject(id);
        Project patch = Project.builder().name("Updated Name").status(ProjectStatus.COMPLETED).build();

        when(projectRepositoryPort.findById(id)).thenReturn(Optional.of(existing));
        when(projectRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Project result = projectService.updateProject(id, patch);

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getStatus()).isEqualTo(ProjectStatus.COMPLETED);
        assertThat(result.getOwnerId()).isEqualTo(existing.getOwnerId());
    }

    @Test
    void updateProject_ShouldKeepExistingFields_WhenPatchFieldsAreNull() {
        UUID id = UUID.randomUUID();
        Project existing = buildProject(id);
        Project patch = Project.builder().build();

        when(projectRepositoryPort.findById(id)).thenReturn(Optional.of(existing));
        when(projectRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Project result = projectService.updateProject(id, patch);

        assertThat(result.getName()).isEqualTo(existing.getName());
        assertThat(result.getStatus()).isEqualTo(existing.getStatus());
    }

    @Test
    void deleteProject_ShouldCallDeleteById_WhenProjectExists() {
        UUID id = UUID.randomUUID();
        when(projectRepositoryPort.findById(id)).thenReturn(Optional.of(buildProject(id)));

        projectService.deleteProject(id);

        verify(projectRepositoryPort).deleteById(id);
    }

    @Test
    void deleteProject_ShouldThrowResourceNotFoundException_WhenNotExists() {
        UUID id = UUID.randomUUID();
        when(projectRepositoryPort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.deleteProject(id))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(projectRepositoryPort, never()).deleteById(any());
    }

    private Project buildProject() {
        return buildProject(UUID.randomUUID());
    }

    private Project buildProject(UUID id) {
        return Project.builder()
                .id(id)
                .name("Test Project")
                .description("A test project")
                .status(ProjectStatus.ACTIVE)
                .ownerId(UUID.randomUUID())
                .build();
    }
}
