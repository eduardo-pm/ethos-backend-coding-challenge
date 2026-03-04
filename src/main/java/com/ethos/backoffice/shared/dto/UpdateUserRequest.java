package com.ethos.backoffice.shared.dto;

import jakarta.validation.constraints.Email;

public record UpdateUserRequest(
        @Email String email,
        String name
) {}
