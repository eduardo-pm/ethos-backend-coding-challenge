package com.ethos.backoffice.infrastructure.adapter.out.persistence.repository;

import com.ethos.backoffice.infrastructure.adapter.out.persistence.entity.ProjectJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectJpaRepository extends JpaRepository<ProjectJpaEntity, UUID> {
}
