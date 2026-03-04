package com.ethos.backoffice.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

@Schema(description = "Payload to update a user — all fields are optional")
public record UpdateUserRequest(
        @Schema(example = "john.updated@example.com") @Email String email,
        @Schema(example = "John Updated") String name
) {}
