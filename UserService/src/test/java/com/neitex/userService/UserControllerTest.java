package com.neitex.userService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.neitex.userService.controller.UserController;
import com.neitex.userService.dto.UserRequestDTO;
import com.neitex.userService.dto.UserResponseDTO;
import com.neitex.userService.service.UserService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserControllerTest {

  @MockBean
  private UserService userService;

  private UserController userController;

  @BeforeEach
  void setUp() {
    userController = new UserController(userService);
  }

  @Test
  void getAllUsersReturnsListOfUsers() {
    List<UserResponseDTO> users = List.of(new UserResponseDTO(), new UserResponseDTO());
    when(userService.getAllUsers()).thenReturn(users);

    List<UserResponseDTO> result = userController.getAllUsers();

    assertNotNull(result);
    assertEquals(2, result.size());
  }

  @Test
  void getUserByIdReturnsUser() {
    UserResponseDTO user = new UserResponseDTO();
    when(userService.getUserById(1L)).thenReturn(Optional.of(user));

    UserResponseDTO result = userController.getUserById(1L);

    assertNotNull(result);
  }

  @Test
  void getUserByIdThrowsNoSuchElementException() {
    when(userService.getUserById(1L)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> userController.getUserById(1L));
  }

  @Test
  void getUserByLoginReturnsUser() {
    UserResponseDTO user = new UserResponseDTO();
    when(userService.getUserByLogin("login")).thenReturn(Optional.of(user));

    UserResponseDTO result = userController.getUserByLogin("login");

    assertNotNull(result);
  }

  @Test
  void getUserByLoginThrowsNoSuchElementException() {
    when(userService.getUserByLogin("login")).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> userController.getUserByLogin("login"));
  }

  @Test
  void updateUserReturnsUpdatedUser() {
    UserRequestDTO userRequest = new UserRequestDTO();
    UserResponseDTO userResponse = new UserResponseDTO();
    when(userService.updateUser(1L, userRequest)).thenReturn(userResponse);

    UserResponseDTO result = userController.updateUser(1L, userRequest);

    assertNotNull(result);
  }

  @Test
  void deleteUserExecutesSuccessfully() {
    doNothing().when(userService).deleteUser(1L);

    userController.deleteUser(1L);

    verify(userService, times(1)).deleteUser(1L);
  }

  @Test
  void createUserReturnsCreatedUser() {
    UserRequestDTO userRequest = new UserRequestDTO();
    UserResponseDTO userResponse = new UserResponseDTO();
    when(userService.createUser(userRequest)).thenReturn(userResponse);

    UserResponseDTO result = userController.createUser(userRequest);

    assertNotNull(result);
  }
}