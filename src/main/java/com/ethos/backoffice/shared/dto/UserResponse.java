package com.ethos.backoffice.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "User data")
public record UserResponse(
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000") UUID id,
        @Schema(example = "john@example.com") String email,
        @Schema(example = "John Doe") String name,
        @Schema(example = "USER", allowableValues = {"USER", "ADMIN"}) String role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
