package com.ethos.backoffice.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Project data")
public record ProjectResponse(
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000") UUID id,
        @Schema(example = "Backoffice API") String name,
        @Schema(example = "REST API for backoffice management") String description,
        @Schema(example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "COMPLETED", "ARCHIVED"}) String status,
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000") UUID ownerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
