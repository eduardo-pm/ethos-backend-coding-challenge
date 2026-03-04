package com.ethos.backoffice.domain.port.in;

import java.util.UUID;

public interface DeleteProjectPort {
    void deleteProject(UUID id);
}
