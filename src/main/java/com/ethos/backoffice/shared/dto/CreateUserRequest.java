package com.ethos.backoffice.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload to create a new user")
public record CreateUserRequest(
        @Schema(example = "john@example.com") @NotBlank @Email String email,
        @Schema(example = "John Doe") @NotBlank String name,
        @Schema(example = "securepass123") @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String password
) {}
