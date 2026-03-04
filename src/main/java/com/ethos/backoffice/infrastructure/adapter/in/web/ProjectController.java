package com.ethos.backoffice.infrastructure.adapter.in.web;

import com.ethos.backoffice.application.usecase.ProjectApplicationService;
import com.ethos.backoffice.domain.model.Project;
import com.ethos.backoffice.domain.model.ProjectStatus;
import com.ethos.backoffice.infrastructure.adapter.in.web.mapper.ProjectWebMapper;
import com.ethos.backoffice.shared.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectApplicationService projectApplicationService;
    private final ProjectWebMapper mapper;

    public ProjectController(ProjectApplicationService projectApplicationService, ProjectWebMapper mapper) {
        this.projectApplicationService = projectApplicationService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @Valid @RequestBody CreateProjectRequest request) {
        Project project = projectApplicationService.createProject(
                request.name(), request.description(), request.ownerId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(mapper.toResponse(project)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ProjectResponse>>> getAllProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<ProjectResponse> projects = projectApplicationService.getAllProjects(page, size).stream()
                .map(mapper::toResponse)
                .toList();
        long total = projectApplicationService.countProjects();
        int totalPages = (int) Math.ceil((double) total / size);

        return ResponseEntity.ok(ApiResponse.success(
                new PagedResponse<>(projects, page, size, total, totalPages)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectById(@PathVariable UUID id) {
        Project project = projectApplicationService.getProjectById(id);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(project)));
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        projectApplicationService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
