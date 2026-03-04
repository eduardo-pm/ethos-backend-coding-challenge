package com.ethos.backoffice.shared.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        String name,
        String description,
        String status,
        UUID ownerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
