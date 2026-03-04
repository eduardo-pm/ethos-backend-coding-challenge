package com.ethos.backoffice.shared.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateProjectRequest(
        @NotBlank String name,
        String description,
        @NotNull UUID ownerId
) {}
