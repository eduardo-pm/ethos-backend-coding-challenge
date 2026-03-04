package com.ethos.backoffice.domain.port.out;

import com.ethos.backoffice.domain.model.Project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepositoryPort {
    Project save(Project project);
    Optional<Project> findById(UUID id);
    List<Project> findAll(int page, int size);
    long count();
    void deleteById(UUID id);
}
