package com.ethos.backoffice.infrastructure.adapter.in.web.mapper;

import com.ethos.backoffice.domain.model.Project;
import com.ethos.backoffice.shared.dto.ProjectResponse;
import org.springframework.stereotype.Component;

@Component
public class ProjectWebMapper {

    public ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStatus().name(),
                project.getOwnerId(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
