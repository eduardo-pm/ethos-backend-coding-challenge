package com.ethos.backoffice.shared.dto;

public record UpdateProjectRequest(
        String name,
        String description,
        String status
) {}
