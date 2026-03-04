package com.ethos.backoffice.infrastructure.adapter.out.persistence;

import com.ethos.backoffice.domain.model.Project;
import com.ethos.backoffice.domain.port.out.ProjectRepositoryPort;
import com.ethos.backoffice.infrastructure.adapter.out.persistence.mapper.ProjectPersistenceMapper;
import com.ethos.backoffice.infrastructure.adapter.out.persistence.repository.ProjectJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProjectPersistenceAdapter implements ProjectRepositoryPort {

    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectPersistenceMapper mapper;

    public ProjectPersistenceAdapter(ProjectJpaRepository projectJpaRepository, ProjectPersistenceMapper mapper) {
        this.projectJpaRepository = projectJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Project save(Project project) {
        return mapper.toDomain(projectJpaRepository.save(mapper.toEntity(project)));
    }

    @Override
    public Optional<Project> findById(UUID id) {
        return projectJpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Project> findAll(int page, int size) {
        return projectJpaRepository.findAll(PageRequest.of(page, size))
                .map(mapper::toDomain)
                .getContent();
    }

    @Override
    public long count() {
        return projectJpaRepository.count();
    }

    @Override
    public void deleteById(UUID id) {
        projectJpaRepository.deleteById(id);
    }
}
