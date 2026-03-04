package com.ethos.backoffice.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Login credentials")
public record LoginRequest(
        @Schema(example = "admin@backoffice.com") @NotBlank @Email String email,
        @Schema(example = "admin") @NotBlank String password
) {}
