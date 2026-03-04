package com.ethos.backoffice.shared.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String name,
        String role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
