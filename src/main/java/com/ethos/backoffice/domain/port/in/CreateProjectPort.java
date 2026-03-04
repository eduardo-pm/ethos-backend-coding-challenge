package com.ethos.backoffice.domain.port.in;

import com.ethos.backoffice.domain.model.Project;

public interface CreateProjectPort {
    Project createProject(Project project);
}
