package com.ethos.backoffice.infrastructure.adapter.in.web;

import com.ethos.backoffice.application.usecase.UserApplicationService;
import com.ethos.backoffice.domain.model.User;
import com.ethos.backoffice.infrastructure.adapter.in.web.mapper.UserWebMapper;
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

@Tag(name = "Users", description = "User management — requires ADMIN role")
@SecurityRequirement(name = "Bearer")
@ApiStandardResponses
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserApplicationService userApplicationService;
    private final UserWebMapper mapper;

    public UserController(UserApplicationService userApplicationService, UserWebMapper mapper) {
        this.userApplicationService = userApplicationService;
        this.mapper = mapper;
    }

    @Operation(summary = "Create user", description = "Register a new user. Public endpoint.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409", description = "Email already in use",
            content = @Content(
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(value = ApiResponseExamples.USER_CONFLICT)))
    })
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userApplicationService.createUser(request.email(), request.name(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(mapper.toResponse(user)));
    }

    @Operation(summary = "List users", description = "Paginated list of all users. Requires ADMIN role.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> getAllUsers(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size) {
        List<UserResponse> users = userApplicationService.getAllUsers(page, size).stream()
                .map(mapper::toResponse)
                .toList();
        long total = userApplicationService.countUsers();
        int totalPages = (int) Math.ceil((double) total / size);

        PagedResponse<UserResponse> pagedResponse = new PagedResponse<>(users, page, size, total, totalPages);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @Operation(summary = "Get user by ID", description = "Requires ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", description = "User not found",
            content = @Content(
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(value = ApiResponseExamples.USER_NOT_FOUND)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        User user = userApplicationService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(user)));
    }

    @Operation(summary = "Update user", description = "Requires ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", description = "User not found",
            content = @Content(
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(value = ApiResponseExamples.USER_NOT_FOUND)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request) {
        User user = userApplicationService.updateUser(id, request.email(), request.name());
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(user)));
    }

    @Operation(summary = "Delete user", description = "Requires ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "User deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", description = "User not found",
            content = @Content(
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(value = ApiResponseExamples.USER_NOT_FOUND)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userApplicationService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
