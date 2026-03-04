package com.ethos.backoffice.application.usecase;

import com.ethos.backoffice.domain.model.Project;
import com.ethos.backoffice.domain.model.ProjectStatus;
import com.ethos.backoffice.domain.port.in.CreateProjectPort;
import com.ethos.backoffice.domain.port.in.DeleteProjectPort;
import com.ethos.backoffice.domain.port.in.GetProjectPort;
import com.ethos.backoffice.domain.port.in.UpdateProjectPort;

import java.util.List;
import java.util.UUID;

public class ProjectApplicationService {

    private final CreateProjectPort createProjectPort;
    private final GetProjectPort getProjectPort;
    private final UpdateProjectPort updateProjectPort;
    private final DeleteProjectPort deleteProjectPort;

    public ProjectApplicationService(CreateProjectPort createProjectPort,
                                     GetProjectPort getProjectPort,
                                     UpdateProjectPort updateProjectPort,
                                     DeleteProjectPort deleteProjectPort) {
        this.createProjectPort = createProjectPort;
        this.getProjectPort = getProjectPort;
        this.updateProjectPort = updateProjectPort;
        this.deleteProjectPort = deleteProjectPort;
    }

    public Project createProject(String name, String description, UUID ownerId) {
        Project project = Project.builder()
                .name(name)
                .description(description)
                .status(ProjectStatus.ACTIVE)
                .ownerId(ownerId)
                .build();
        return createProjectPort.createProject(project);
    }

    public Project getProjectById(UUID id) {
        return getProjectPort.getProjectById(id);
    }

    public List<Project> getAllProjects(int page, int size) {
        return getProjectPort.getAllProjects(page, size);
    }

    public long countProjects() {
        return getProjectPort.countProjects();
    }

    public Project updateProject(UUID id, String name, String description, ProjectStatus status) {
        Project patch = Project.builder()
                .name(name)
                .description(description)
                .status(status)
                .build();
        return updateProjectPort.updateProject(id, patch);
    }

    public void deleteProject(UUID id) {
        deleteProjectPort.deleteProject(id);
    }
}
