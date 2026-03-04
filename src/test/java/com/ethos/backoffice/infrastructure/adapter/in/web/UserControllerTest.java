package com.ethos.backoffice.infrastructure.adapter.in.web;

import com.ethos.backoffice.application.usecase.UserApplicationService;
import com.ethos.backoffice.domain.model.Role;
import com.ethos.backoffice.domain.model.User;
import com.ethos.backoffice.infrastructure.adapter.in.web.mapper.UserWebMapper;
import com.ethos.backoffice.infrastructure.security.JwtAuthenticationFilter;
import com.ethos.backoffice.infrastructure.security.JwtService;
import com.ethos.backoffice.shared.dto.CreateUserRequest;
import com.ethos.backoffice.shared.dto.UpdateUserRequest;
import com.ethos.backoffice.shared.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({com.ethos.backoffice.infrastructure.security.SecurityConfig.class, UserWebMapper.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserApplicationService userApplicationService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() throws Exception {
        doAnswer(inv -> {
            ((jakarta.servlet.FilterChain) inv.getArgument(2))
                    .doFilter(inv.getArgument(0), inv.getArgument(1));
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }

    @Test
    void createUser_ShouldReturn201_WhenRequestIsValid() throws Exception {
        User user = buildUser();
        when(userApplicationService.createUser(anyString(), anyString(), anyString())).thenReturn(user);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateUserRequest("test@test.com", "Test User", "password123"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("test@test.com"));
    }

    @Test
    void createUser_ShouldReturn400_WhenEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateUserRequest("not-an-email", "Test", "password123"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_ShouldReturn400_WhenPasswordIsTooShort() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateUserRequest("test@test.com", "Test", "short"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_ShouldReturn200_WhenAdmin() throws Exception {
        when(userApplicationService.getAllUsers(0, 10)).thenReturn(List.of(buildUser()));
        when(userApplicationService.countUsers()).thenReturn(1L);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void getAllUsers_ShouldReturn401_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_ShouldReturn403_WhenNotAdmin() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_ShouldReturn200_WhenUserExists() throws Exception {
        UUID id = UUID.randomUUID();
        when(userApplicationService.getUserById(id)).thenReturn(buildUser(id));

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_ShouldReturn404_WhenUserNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(userApplicationService.getUserById(id))
                .thenThrow(new ResourceNotFoundException("User", id));

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_ShouldReturn200_WhenValid() throws Exception {
        UUID id = UUID.randomUUID();
        when(userApplicationService.updateUser(eq(id), anyString(), anyString())).thenReturn(buildUser(id));

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UpdateUserRequest("new@test.com", "New Name"))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_ShouldReturn204_WhenUserExists() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(userApplicationService).deleteUser(id);

        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isNoContent());
    }

    private User buildUser() {
        return buildUser(UUID.randomUUID());
    }

    private User buildUser(UUID id) {
        return User.builder()
                .id(id)
                .email("test@test.com")
                .name("Test User")
                .role(Role.USER)
                .build();
    }
}
