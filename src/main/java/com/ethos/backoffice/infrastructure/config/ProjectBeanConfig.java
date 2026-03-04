package com.ethos.backoffice.infrastructure.config;

import com.ethos.backoffice.application.usecase.ProjectApplicationService;
import com.ethos.backoffice.domain.port.out.ProjectRepositoryPort;
import com.ethos.backoffice.domain.service.ProjectService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectBeanConfig {

    @Bean
    public ProjectService projectService(ProjectRepositoryPort projectRepositoryPort) {
        return new ProjectService(projectRepositoryPort);
    }

    @Bean
    public ProjectApplicationService projectApplicationService(ProjectService projectService) {
        return new ProjectApplicationService(projectService, projectService, projectService, projectService);
    }
}
