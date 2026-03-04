package com.ethos.backoffice.domain.service;

import com.ethos.backoffice.domain.model.Role;
import com.ethos.backoffice.domain.model.User;
import com.ethos.backoffice.domain.port.out.UserRepositoryPort;
import com.ethos.backoffice.shared.exception.DuplicateResourceException;
import com.ethos.backoffice.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepositoryPort);
    }

    @Test
    void createUser_ShouldReturnSavedUser_WhenEmailIsUnique() {
        User user = buildUser();
        when(userRepositoryPort.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepositoryPort.save(user)).thenReturn(user);

        User result = userService.createUser(user);

        assertThat(result).isEqualTo(user);
        verify(userRepositoryPort).save(user);
    }

    @Test
    void createUser_ShouldThrowDuplicateResourceException_WhenEmailAlreadyExists() {
        User user = buildUser();
        when(userRepositoryPort.existsByEmail(user.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(user))
                .isInstanceOf(DuplicateResourceException.class);

        verify(userRepositoryPort, never()).save(any());
    }

    @Test
    void getUserById_ShouldReturnUser_WhenExists() {
        UUID id = UUID.randomUUID();
        User user = buildUser(id);
        when(userRepositoryPort.findById(id)).thenReturn(Optional.of(user));

        User result = userService.getUserById(id);

        assertThat(result).isEqualTo(user);
    }

    @Test
    void getUserById_ShouldThrowResourceNotFoundException_WhenNotExists() {
        UUID id = UUID.randomUUID();
        when(userRepositoryPort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAllUsers_ShouldReturnPagedList() {
        List<User> users = List.of(buildUser(), buildUser());
        when(userRepositoryPort.findAll(0, 10)).thenReturn(users);

        List<User> result = userService.getAllUsers(0, 10);

        assertThat(result).hasSize(2);
    }

    @Test
    void updateUser_ShouldUpdateFields_WhenUserExists() {
        UUID id = UUID.randomUUID();
        User existing = buildUser(id);
        User patch = User.builder().email("new@test.com").name("New Name").build();
        User updated = User.builder().id(id).email("new@test.com").name("New Name")
                .password(existing.getPassword()).role(existing.getRole()).build();

        when(userRepositoryPort.findById(id)).thenReturn(Optional.of(existing));
        when(userRepositoryPort.existsByEmail("new@test.com")).thenReturn(false);
        when(userRepositoryPort.save(any())).thenReturn(updated);

        User result = userService.updateUser(id, patch);

        assertThat(result.getEmail()).isEqualTo("new@test.com");
        assertThat(result.getName()).isEqualTo("New Name");
    }

    @Test
    void updateUser_ShouldThrowDuplicateResourceException_WhenNewEmailAlreadyTaken() {
        UUID id = UUID.randomUUID();
        User existing = buildUser(id);
        User patch = User.builder().email("taken@test.com").build();

        when(userRepositoryPort.findById(id)).thenReturn(Optional.of(existing));
        when(userRepositoryPort.existsByEmail("taken@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(id, patch))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void deleteUser_ShouldCallDeleteById_WhenUserExists() {
        UUID id = UUID.randomUUID();
        when(userRepositoryPort.findById(id)).thenReturn(Optional.of(buildUser(id)));

        userService.deleteUser(id);

        verify(userRepositoryPort).deleteById(id);
    }

    @Test
    void deleteUser_ShouldThrowResourceNotFoundException_WhenUserNotExists() {
        UUID id = UUID.randomUUID();
        when(userRepositoryPort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(id))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userRepositoryPort, never()).deleteById(any());
    }

    private User buildUser() {
        return buildUser(UUID.randomUUID());
    }

    private User buildUser(UUID id) {
        return User.builder()
                .id(id)
                .email("user@test.com")
                .name("Test User")
                .password("encoded-password")
                .role(Role.USER)
                .build();
    }
}
