package com.ethos.backoffice.domain.service;

import com.ethos.backoffice.domain.model.User;
import com.ethos.backoffice.domain.port.in.CreateUserPort;
import com.ethos.backoffice.domain.port.in.DeleteUserPort;
import com.ethos.backoffice.domain.port.in.GetUserPort;
import com.ethos.backoffice.domain.port.in.UpdateUserPort;
import com.ethos.backoffice.domain.port.out.UserRepositoryPort;
import com.ethos.backoffice.shared.exception.DuplicateResourceException;
import com.ethos.backoffice.shared.exception.ResourceNotFoundException;

import java.util.List;
import java.util.UUID;

public class UserService implements CreateUserPort, GetUserPort, UpdateUserPort, DeleteUserPort {

    private final UserRepositoryPort userRepositoryPort;

    public UserService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User createUser(User user) {
        if (userRepositoryPort.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("User", "email", user.getEmail());
        }
        return userRepositoryPort.save(user);
    }

    @Override
    public User getUserById(UUID id) {
        return userRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    @Override
    public List<User> getAllUsers(int page, int size) {
        return userRepositoryPort.findAll(page, size);
    }

    @Override
    public long countUsers() {
        return userRepositoryPort.count();
    }

    @Override
    public User updateUser(UUID id, User user) {
        User existing = getUserById(id);

        if (user.getEmail() != null && !user.getEmail().equals(existing.getEmail())
                && userRepositoryPort.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("User", "email", user.getEmail());
        }

        User updated = User.builder()
                .id(existing.getId())
                .email(user.getEmail() != null ? user.getEmail() : existing.getEmail())
                .name(user.getName() != null ? user.getName() : existing.getName())
                .password(existing.getPassword())
                .role(existing.getRole())
                .createdAt(existing.getCreatedAt())
                .updatedAt(existing.getUpdatedAt())
                .build();

        return userRepositoryPort.save(updated);
    }

    @Override
    public void deleteUser(UUID id) {
        getUserById(id);
        userRepositoryPort.deleteById(id);
    }
}
