package com.ethos.backoffice.application.usecase;

import com.ethos.backoffice.domain.model.Role;
import com.ethos.backoffice.domain.model.User;
import com.ethos.backoffice.domain.port.in.CreateUserPort;
import com.ethos.backoffice.domain.port.in.DeleteUserPort;
import com.ethos.backoffice.domain.port.in.GetUserPort;
import com.ethos.backoffice.domain.port.in.UpdateUserPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

public class UserApplicationService {

    private final CreateUserPort createUserPort;
    private final GetUserPort getUserPort;
    private final UpdateUserPort updateUserPort;
    private final DeleteUserPort deleteUserPort;
    private final PasswordEncoder passwordEncoder;

    public UserApplicationService(CreateUserPort createUserPort,
                       GetUserPort getUserPort,
                       UpdateUserPort updateUserPort,
                       DeleteUserPort deleteUserPort,
                       PasswordEncoder passwordEncoder) {
        this.createUserPort = createUserPort;
        this.getUserPort = getUserPort;
        this.updateUserPort = updateUserPort;
        this.deleteUserPort = deleteUserPort;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(String email, String name, String password) {
        User user = User.builder()
                .email(email)
                .name(name)
                .password(passwordEncoder.encode(password))
                .role(Role.USER)
                .build();
        return createUserPort.createUser(user);
    }

    public User getUserById(UUID id) {
        return getUserPort.getUserById(id);
    }

    public List<User> getAllUsers(int page, int size) {
        return getUserPort.getAllUsers(page, size);
    }

    public long countUsers() {
        return getUserPort.countUsers();
    }

    public User updateUser(UUID id, String email, String name) {
        User user = User.builder()
                .email(email)
                .name(name)
                .build();
        return updateUserPort.updateUser(id, user);
    }

    public void deleteUser(UUID id) {
        deleteUserPort.deleteUser(id);
    }
}
