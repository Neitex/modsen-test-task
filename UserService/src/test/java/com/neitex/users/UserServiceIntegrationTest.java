package com.neitex.users;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neitex.users.dto.LoginRequestDTO;
import com.neitex.users.dto.UserRequestDTO;
import com.neitex.users.dto.UserResponseDTO;
import com.neitex.users.model.UserRole;
import com.neitex.users.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@TestPropertySource(locations = "classpath:application.properties")
@SpringBootTest(classes = UserServiceApplication.class)
@AutoConfigureMockMvc
public class UserServiceIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserService userService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void testGetAllUsers() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/users/all"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  public void testGetUserById() throws Exception {
    UserRequestDTO requestDTO = new UserRequestDTO();
    requestDTO.setName("Test User");
    requestDTO.setRole(UserRole.VIEWER);
    requestDTO.setLogin("testGetUserById");
    requestDTO.setPassword("password");
    UserResponseDTO user = userService.createUser(requestDTO);
    mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", user.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.login", equalTo("testGetUserById")))
        .andExpect(jsonPath("$.name", equalTo("Test User")))
        .andExpect(jsonPath("$.role", equalTo("VIEWER")));
  }

  @Test
  public void testGetUserByLogin() throws Exception {
    UserRequestDTO requestDTO = new UserRequestDTO();
    requestDTO.setName("Test User");
    requestDTO.setRole(UserRole.VIEWER);
    requestDTO.setLogin("testLoginByLogin");
    requestDTO.setPassword("password");
    UserResponseDTO user = userService.createUser(requestDTO);
    mockMvc.perform(MockMvcRequestBuilders.get("/users/by-login/{login}", user.getLogin()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.login", equalTo("testLoginByLogin")))
        .andExpect(jsonPath("$.name", equalTo("Test User")))
        .andExpect(jsonPath("$.role", equalTo("VIEWER")));
  }

  @Test
  public void testCreateUser() throws Exception {
    UserRequestDTO requestDTO = new UserRequestDTO();
    requestDTO.setName("Test User");
    requestDTO.setRole(UserRole.VIEWER);
    requestDTO.setLogin("testCreate");
    requestDTO.setPassword("password");
    mockMvc.perform(MockMvcRequestBuilders.post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.login", equalTo("testCreate")))
        .andExpect(jsonPath("$.name", equalTo("Test User")))
        .andExpect(jsonPath("$.role", equalTo("VIEWER")));
  }

  @Test
  public void testUpdateUser() throws Exception {
    UserRequestDTO creationDTO = new UserRequestDTO();
    creationDTO.setName("Test User");
    creationDTO.setRole(UserRole.VIEWER);
    creationDTO.setLogin("testUpdate");
    creationDTO.setPassword("password");
    UserResponseDTO user = userService.createUser(creationDTO);
    UserRequestDTO updateDTO = new UserRequestDTO();
    updateDTO.setName("Updated User");
    updateDTO.setRole(UserRole.EDITOR);
    updateDTO.setLogin("updatedLogin");
    updateDTO.setPassword("password");
    mockMvc.perform(MockMvcRequestBuilders.patch("/users/{id}", user.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.login", equalTo("updatedLogin")))
        .andExpect(jsonPath("$.name", equalTo("Updated User")))
        .andExpect(jsonPath("$.role", equalTo("EDITOR")));
  }

  @Test
  public void testDeleteUser() throws Exception {
    UserRequestDTO requestDTO = new UserRequestDTO();
    requestDTO.setName("Test User");
    requestDTO.setRole(UserRole.VIEWER);
    requestDTO.setLogin("deleteUser");
    requestDTO.setPassword("password");
    UserResponseDTO user = userService.createUser(requestDTO);
    mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", user.getId()))
        .andExpect(status().isOk());
  }

  @Test
  public void testJwtAuth() throws Exception {
    // setup
    UserRequestDTO requestDTO = new UserRequestDTO();
    requestDTO.setName("Test User");
    requestDTO.setRole(UserRole.VIEWER);
    requestDTO.setLogin("testJwtAuth");
    requestDTO.setPassword("password");
    userService.createUser(requestDTO);

    LoginRequestDTO loginRequestDTO = new LoginRequestDTO("testJwtAuth", "password");
    mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequestDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").isString());
  }

  @Test
  public void testJwtAuthInvalid() throws Exception {
    // setup
    UserRequestDTO requestDTO = new UserRequestDTO();
    requestDTO.setName("Test User");
    requestDTO.setRole(UserRole.VIEWER);
    requestDTO.setLogin("testJwtAuthInvalid");
    requestDTO.setPassword("password");
    userService.createUser(requestDTO);

    LoginRequestDTO loginRequestDTO = new LoginRequestDTO("testJwtAuthInvalid", "invalidPassword");
    mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequestDTO)))
        .andExpect(status().isUnauthorized());
  }
}
