package com.neitex.userService.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.neitex.userService.dto.UserRequestDTO;
import com.neitex.userService.dto.UserResponseDTO;
import com.neitex.userService.exception.BadLoginCredentials;
import com.neitex.userService.exception.NoSuchUserException;
import com.neitex.userService.exception.UsernameTakenException;
import com.neitex.userService.model.User;
import com.neitex.userService.model.UserRole;
import com.neitex.userService.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceTest {

  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;
  private ModelMapper modelMapper;
  private JwtService jwtService;
  private UserService userService;

  @BeforeEach
  void setUp() {
    userRepository = Mockito.mock(UserRepository.class);
    passwordEncoder = Mockito.mock(PasswordEncoder.class);
    modelMapper = Mockito.mock(ModelMapper.class);
    jwtService = Mockito.mock(JwtService.class);
    userService = new UserService(userRepository, passwordEncoder, modelMapper, jwtService);
  }

  @Test
  void getUserById_returnsUserResponseDTO_whenUserExists() {
    User user = new User();
    user.setId(1L);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(modelMapper.map(user, UserResponseDTO.class)).thenReturn(new UserResponseDTO());

    UserResponseDTO result = userService.getUserById(1L);

    assertNotNull(result);
  }

  @Test
  void getUserById_throwsNoSuchUserException_whenUserDoesNotExist() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NoSuchUserException.class, () -> userService.getUserById(1L));
  }

  @Test
  void createUser_returnsUserResponseDTO_whenUserIsCreated() {
    UserRequestDTO userRequestDTO = new UserRequestDTO();
    userRequestDTO.setLogin("testLogin");
    userRequestDTO.setPassword("testPassword");
    User user = new User();
    when(userRepository.findByLogin("testLogin")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("testPassword")).thenReturn("encodedPassword");
    when(modelMapper.map(userRequestDTO, User.class)).thenReturn(user);
    when(userRepository.save(user)).thenReturn(user);
    when(modelMapper.map(user, UserResponseDTO.class)).thenReturn(new UserResponseDTO());

    UserResponseDTO result = userService.createUser(userRequestDTO);

    assertNotNull(result);
  }

  @Test
  void createUser_throwsUsernameTakenException_whenLoginAlreadyExists() {
    UserRequestDTO userRequestDTO = new UserRequestDTO();
    userRequestDTO.setLogin("testLogin");
    userRequestDTO.setPassword("testPassword");
    when(userRepository.findByLogin("testLogin")).thenReturn(Optional.of(new User()));

    assertThrows(UsernameTakenException.class, () -> userService.createUser(userRequestDTO));
  }

  @Test
  void deleteUser_deletesUser_whenUserExists() {
    userService.deleteUser(1L);

    verify(userRepository).deleteById(1L);
  }

  @Test
  void updateUser_returnsUpdatedUserResponseDTO_whenUserIsUpdated() {
    UserRequestDTO userRequestDTO = new UserRequestDTO();
    userRequestDTO.setLogin("newLogin");
    User user = new User();
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.findByLogin("newLogin")).thenReturn(Optional.empty());
    when(userRepository.save(user)).thenReturn(user);
    when(modelMapper.map(user, UserResponseDTO.class)).thenReturn(new UserResponseDTO());

    UserResponseDTO result = userService.updateUser(1L, userRequestDTO);

    assertNotNull(result);
  }

  @Test
  void updateUser_throwsNoSuchElementException_whenUserDoesNotExist() {
    UserRequestDTO userRequestDTO = new UserRequestDTO();
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> userService.updateUser(1L, userRequestDTO));
  }

  @Test
  void getAllUsers_returnsListOfUserResponseDTOs() {
    User user = new User();
    when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
    when(modelMapper.map(user, UserResponseDTO.class)).thenReturn(new UserResponseDTO());

    List<UserResponseDTO> result = userService.getAllUsers();

    assertFalse(result.isEmpty());
  }

  @Test
  void issueAuthToken_returnsToken_whenLoginAndPasswordAreCorrect() {
    User user = new User();
    user.setEncryptedPassword("encodedPassword");
    when(userRepository.findByLogin("testLogin")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("testPassword", "encodedPassword")).thenReturn(true);
    when(jwtService.issueAuthToken(user)).thenReturn("token");

    String result = userService.issueAuthToken("testLogin", "testPassword");

    assertEquals("token", result);
  }

  @Test
  void issueAuthToken_throwsBadUsernameOrLoginException_whenLoginOrPasswordAreIncorrect() {
    when(userRepository.findByLogin("testLogin")).thenReturn(Optional.empty());

    assertThrows(BadLoginCredentials.class,
        () -> userService.issueAuthToken("testLogin", "testPassword"));
  }

  @Test
  void exchangeTokenToUserInfo_returnsUserDataToken_whenTokenIsValid() {
    User user = new User();
    when(jwtService.getUserIdFromToken("token")).thenReturn(1L);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(jwtService.verifyAuthToken("token", user)).thenReturn(true);
    when(jwtService.issueUserDataToken(user)).thenReturn("userDataToken");

    Optional<String> result = userService.exchangeTokenToUserInfo("token");

    assertTrue(result.isPresent());
    assertEquals("userDataToken", result.get());
  }

  @Test
  void exchangeTokenToUserInfo_returnsEmptyOptional_whenTokenIsInvalid() {
    when(jwtService.getUserIdFromToken("token")).thenReturn(null);

    Optional<String> result = userService.exchangeTokenToUserInfo("token");

    assertTrue(result.isEmpty());
  }

  @Test
  void issueAuthToken_throwsWhenPasswordIsIncorrect() {
    User user = new User();
    user.setEncryptedPassword("encodedPassword");
    when(userRepository.findByLogin("testLogin")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("testPassword", "encodedPassword")).thenReturn(false);

    Assertions.assertThrows(BadLoginCredentials.class,
        () -> userService.issueAuthToken("testLogin", "testPassword"));
  }

  @Test
  void exchangeTokenToUserInfo_returnsEmptyWhenTokenIsBad() {
    User user = new User();
    when(jwtService.getUserIdFromToken("token")).thenReturn(1L);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(jwtService.verifyAuthToken("token", user)).thenReturn(false);

    Optional<String> result = userService.exchangeTokenToUserInfo("token");

    assertTrue(result.isEmpty());
  }

  @Test
  void updateUser_ShouldUpdateUserFields() {
    // Arrange
    Long userId = 1L;
    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setLogin("oldLogin");
    existingUser.setName("oldName");
    existingUser.setRole(UserRole.VIEWER);

    UserRequestDTO updateRequest = new UserRequestDTO();
    updateRequest.setLogin("newLogin");
    updateRequest.setName("newName");
    updateRequest.setRole(UserRole.EDITOR);
    updateRequest.setPassword("newPassword");

    // Mock repository responses
    when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
    when(userRepository.findByLogin("newLogin")).thenReturn(Optional.empty());
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

    // Mock ModelMapper to return a simple DTO
    UserResponseDTO expectedResponse = new UserResponseDTO();
    when(modelMapper.map(any(User.class), any())).thenReturn(expectedResponse);

    // Act
    UserResponseDTO result = userService.updateUser(userId, updateRequest);
    assertEquals(expectedResponse, result);

    // Assert
    verify(userRepository).findById(userId);
    verify(userRepository).findByLogin("newLogin");

    // Capture the User object being saved
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());

    User savedUser = userCaptor.getValue();
    assertEquals("newLogin", savedUser.getLogin());
    assertEquals("newName", savedUser.getName());
    assertEquals(UserRole.EDITOR, savedUser.getRole());
    assertEquals("encodedNewPassword", savedUser.getEncryptedPassword());
  }

  @Test
  void updateUser_throwsWhenUsernameIsTaken() {
    User existingUser = new User();
    existingUser.setId(1L);
    existingUser.setLogin("oldLogin");

    User updateUser = new User();
    updateUser.setId(2L);
    updateUser.setLogin("login");
    UserRequestDTO updateRequest = new UserRequestDTO();
    updateRequest.setLogin("oldLogin");

    when(userRepository.findByLogin("oldLogin")).thenReturn(Optional.of(existingUser));
    when(userRepository.findByLogin("login")).thenReturn(Optional.of(updateUser));
    when(userRepository.findById(2L)).thenReturn(Optional.of(updateUser));
    assertThrows(UsernameTakenException.class, () -> userService.updateUser(2L, updateRequest));
  }

  @Test
  void getUserByLogin_returnsUser() {
    User user = new User();
    user.setLogin("testLogin");
    when(userRepository.findByLogin("testLogin")).thenReturn(Optional.of(user));
    UserResponseDTO expected = new UserResponseDTO();
    when(modelMapper.map(user, UserResponseDTO.class)).thenReturn(expected);
    UserResponseDTO result = userService.getUserByLogin("testLogin");
    assertEquals(expected, result);
  }

  @Test
  void getUserByLogin_throwsNoSuchUserException() {
    when(userRepository.findByLogin("testLogin")).thenReturn(Optional.empty());
    assertThrows(NoSuchUserException.class, () -> userService.getUserByLogin("testLogin"));
  }

}