package com.ethos.backoffice.infrastructure.adapter.in.web;

import com.ethos.backoffice.application.usecase.ProjectApplicationService;
import com.ethos.backoffice.domain.model.Project;
import com.ethos.backoffice.domain.model.ProjectStatus;
import com.ethos.backoffice.infrastructure.adapter.in.web.mapper.ProjectWebMapper;
import com.ethos.backoffice.infrastructure.security.JwtAuthenticationFilter;
import com.ethos.backoffice.infrastructure.security.JwtService;
import com.ethos.backoffice.shared.dto.CreateProjectRequest;
import com.ethos.backoffice.shared.dto.UpdateProjectRequest;
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

@WebMvcTest(ProjectController.class)
@Import({com.ethos.backoffice.infrastructure.security.SecurityConfig.class, ProjectWebMapper.class})
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectApplicationService projectApplicationService;

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
    @WithMockUser
    void createProject_ShouldReturn201_WhenRequestIsValid() throws Exception {
        UUID ownerId = UUID.randomUUID();
        Project project = buildProject();
        when(projectApplicationService.createProject(anyString(), any(), eq(ownerId))).thenReturn(project);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateProjectRequest("My Project", "Description", ownerId))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Test Project"));
    }

    @Test
    void createProject_ShouldReturn401_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateProjectRequest("My Project", null, UUID.randomUUID()))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void createProject_ShouldReturn400_WhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateProjectRequest("", "Description", UUID.randomUUID()))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getAllProjects_ShouldReturn200_WhenAuthenticated() throws Exception {
        when(projectApplicationService.getAllProjects(0, 10)).thenReturn(List.of(buildProject()));
        when(projectApplicationService.countProjects()).thenReturn(1L);

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void getAllProjects_ShouldReturn401_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getProjectById_ShouldReturn200_WhenExists() throws Exception {
        UUID id = UUID.randomUUID();
        when(projectApplicationService.getProjectById(id)).thenReturn(buildProject(id));

        mockMvc.perform(get("/api/projects/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id.toString()));
    }

    @Test
    @WithMockUser
    void getProjectById_ShouldReturn404_WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(projectApplicationService.getProjectById(id))
                .thenThrow(new ResourceNotFoundException("Project", id));

        mockMvc.perform(get("/api/projects/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void updateProject_ShouldReturn200_WhenValid() throws Exception {
        UUID id = UUID.randomUUID();
        when(projectApplicationService.updateProject(eq(id), any(), any(), any())).thenReturn(buildProject(id));

        mockMvc.perform(put("/api/projects/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UpdateProjectRequest("Updated", "New desc", "COMPLETED"))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteProject_ShouldReturn204_WhenExists() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(projectApplicationService).deleteProject(id);

        mockMvc.perform(delete("/api/projects/{id}", id))
                .andExpect(status().isNoContent());
    }

    private Project buildProject() {
        return buildProject(UUID.randomUUID());
    }

    private Project buildProject(UUID id) {
        return Project.builder()
                .id(id)
                .name("Test Project")
                .description("A test project")
                .status(ProjectStatus.ACTIVE)
                .ownerId(UUID.randomUUID())
                .build();
    }
}
