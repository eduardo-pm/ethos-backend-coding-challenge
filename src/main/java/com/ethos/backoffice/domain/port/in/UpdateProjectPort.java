package com.ethos.backoffice.domain.port.in;

import com.ethos.backoffice.domain.model.Project;

import java.util.UUID;

public interface UpdateProjectPort {
    Project updateProject(UUID id, Project project);
}
