package com.ethos.backoffice.infrastructure.adapter.in.web;

import com.ethos.backoffice.application.usecase.UserApplicationService;
import com.ethos.backoffice.domain.model.User;
import com.ethos.backoffice.infrastructure.adapter.in.web.mapper.UserWebMapper;
import com.ethos.backoffice.shared.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserApplicationService userApplicationService;
    private final UserWebMapper mapper;

    public UserController(UserApplicationService userApplicationService, UserWebMapper mapper) {
        this.userApplicationService = userApplicationService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userApplicationService.createUser(request.email(), request.name(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(mapper.toResponse(user)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<UserResponse> users = userApplicationService.getAllUsers(page, size).stream()
                .map(mapper::toResponse)
                .toList();
        long total = userApplicationService.countUsers();
        int totalPages = (int) Math.ceil((double) total / size);

        PagedResponse<UserResponse> pagedResponse = new PagedResponse<>(users, page, size, total, totalPages);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        User user = userApplicationService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(user)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request) {
        User user = userApplicationService.updateUser(id, request.email(), request.name());
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(user)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userApplicationService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
