package com.ethos.backoffice.domain.service;

import com.ethos.backoffice.domain.model.Project;
import com.ethos.backoffice.domain.port.in.CreateProjectPort;
import com.ethos.backoffice.domain.port.in.DeleteProjectPort;
import com.ethos.backoffice.domain.port.in.GetProjectPort;
import com.ethos.backoffice.domain.port.in.UpdateProjectPort;
import com.ethos.backoffice.domain.port.out.ProjectRepositoryPort;
import com.ethos.backoffice.shared.exception.ResourceNotFoundException;

import java.util.List;
import java.util.UUID;

public class ProjectService implements CreateProjectPort, GetProjectPort, UpdateProjectPort, DeleteProjectPort {

    private final ProjectRepositoryPort projectRepositoryPort;

    public ProjectService(ProjectRepositoryPort projectRepositoryPort) {
        this.projectRepositoryPort = projectRepositoryPort;
    }

    @Override
    public Project createProject(Project project) {
        return projectRepositoryPort.save(project);
    }

    @Override
    public Project getProjectById(UUID id) {
        return projectRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
    }

    @Override
    public List<Project> getAllProjects(int page, int size) {
        return projectRepositoryPort.findAll(page, size);
    }

    @Override
    public long countProjects() {
        return projectRepositoryPort.count();
    }

    @Override
    public Project updateProject(UUID id, Project project) {
        Project existing = getProjectById(id);

        Project updated = Project.builder()
                .id(existing.getId())
                .name(project.getName() != null ? project.getName() : existing.getName())
                .description(project.getDescription() != null ? project.getDescription() : existing.getDescription())
                .status(project.getStatus() != null ? project.getStatus() : existing.getStatus())
                .ownerId(existing.getOwnerId())
                .createdAt(existing.getCreatedAt())
                .updatedAt(existing.getUpdatedAt())
                .build();

        return projectRepositoryPort.save(updated);
    }

    @Override
    public void deleteProject(UUID id) {
        getProjectById(id);
        projectRepositoryPort.deleteById(id);
    }
}
