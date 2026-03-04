package com.ethos.backoffice.infrastructure.adapter.in.web;

import com.ethos.backoffice.application.usecase.ProjectApplicationService;
import com.ethos.backoffice.domain.model.Project;
import com.ethos.backoffice.domain.model.ProjectStatus;
import com.ethos.backoffice.infrastructure.adapter.in.web.mapper.ProjectWebMapper;
import com.ethos.backoffice.shared.dto.*;
import com.ethos.backoffice.shared.dto.ApiErrorResponse;
import com.ethos.backoffice.shared.openapi.ApiResponseExamples;
import com.ethos.backoffice.shared.openapi.ApiStandardResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Projects", description = "Project management — requires authentication")
@SecurityRequirement(name = "Bearer")
@ApiStandardResponses
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectApplicationService projectApplicationService;
    private final ProjectWebMapper mapper;

    public ProjectController(ProjectApplicationService projectApplicationService, ProjectWebMapper mapper) {
        this.projectApplicationService = projectApplicationService;
        this.mapper = mapper;
    }

    @Operation(summary = "Create project", description = "Create a new project linked to an owner.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Project created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", description = "Invalid input",
            content = @Content(
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(value = ApiResponseExamples.PROJECT_VALIDATION)))
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @Valid @RequestBody CreateProjectRequest request) {
        Project project = projectApplicationService.createProject(
                request.name(), request.description(), request.ownerId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(mapper.toResponse(project)));
    }

    @Operation(summary = "List projects", description = "Paginated list of all projects.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Projects retrieved")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ProjectResponse>>> getAllProjects(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size) {
        List<ProjectResponse> projects = projectApplicationService.getAllProjects(page, size).stream()
                .map(mapper::toResponse)
                .toList();
        long total = projectApplicationService.countProjects();
        int totalPages = (int) Math.ceil((double) total / size);

        return ResponseEntity.ok(ApiResponse.success(
                new PagedResponse<>(projects, page, size, total, totalPages)));
    }

    @Operation(summary = "Get project by ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", description = "Project not found",
            content = @Content(
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(value = ApiResponseExamples.PROJECT_NOT_FOUND)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectById(@PathVariable UUID id) {
        Project project = projectApplicationService.getProjectById(id);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(project)));
    }

    @Operation(summary = "Update project", description = "Updatable fields: name, description, status (ACTIVE, INACTIVE, COMPLETED, ARCHIVED).")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", description = "Project not found",
            content = @Content(
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(value = ApiResponseExamples.PROJECT_NOT_FOUND)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProjectRequest request) {
        ProjectStatus status = request.status() != null
                ? ProjectStatus.valueOf(request.status())
                : null;
        Project project = projectApplicationService.updateProject(
                id, request.name(), request.description(), status);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(project)));
    }

    @Operation(summary = "Delete project")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Project deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", description = "Project not found",
            content = @Content(
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(value = ApiResponseExamples.PROJECT_NOT_FOUND)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        projectApplicationService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
