package com.ethos.backoffice.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Payload to create a new project")
public record CreateProjectRequest(
        @Schema(example = "Backoffice API") @NotBlank String name,
        @Schema(example = "REST API for backoffice management") String description,
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000") @NotNull UUID ownerId
) {}
