package com.ethos.backoffice.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload to update a project — all fields are optional")
public record UpdateProjectRequest(
        @Schema(example = "Updated Project Name") String name,
        @Schema(example = "Updated description") String description,
        @Schema(example = "COMPLETED", allowableValues = {"ACTIVE", "INACTIVE", "COMPLETED", "ARCHIVED"}) String status
) {}
