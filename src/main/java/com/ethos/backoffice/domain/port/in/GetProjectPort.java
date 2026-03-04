package com.ethos.backoffice.domain.port.in;

import com.ethos.backoffice.domain.model.Project;

import java.util.List;
import java.util.UUID;

public interface GetProjectPort {
    Project getProjectById(UUID id);
    List<Project> getAllProjects(int page, int size);
    long countProjects();
}
