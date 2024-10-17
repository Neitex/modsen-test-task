package com.neitex.userService.service;

import com.neitex.userService.dto.UserRequestDTO;
import com.neitex.userService.dto.UserResponseDTO;
import com.neitex.userService.exception.BadUsernameOrLoginException;
import com.neitex.userService.exception.UsernameTakenException;
import com.neitex.userService.model.User;
import com.neitex.userService.repository.UserRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final ModelMapper modelMapper;
  private final JwtService jwtService;

  public Optional<UserResponseDTO> getUserById(Long id) {
    return userRepository.findById(id)
        .map(user -> modelMapper.map(user, UserResponseDTO.class));
  }

  public Optional<UserResponseDTO> getUserByLogin(String login) {
    return userRepository.findByLogin(login)
        .map(user -> modelMapper.map(user, UserResponseDTO.class));
  }

  public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
    Objects.requireNonNull(userRequestDTO.getLogin());
    Objects.requireNonNull(userRequestDTO.getPassword());
    User user = modelMapper.map(userRequestDTO, User.class);
    if (userRepository.findByLogin(userRequestDTO.getLogin()).isPresent()) {
      throw new UsernameTakenException("User with such login already exists");
    }
    user.setEncryptedPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
    user.setTokenSalt(JwtService.generateTokenSalt());
    return modelMapper.map(userRepository.save(user), UserResponseDTO.class);
  }

  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }

  public UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO) {
    User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found"));
    if (userRequestDTO.getLogin() != null) {
      if (userRepository.findByLogin(userRequestDTO.getLogin()).isPresent()) {
        throw new UsernameTakenException("User with such login already exists");
      }
      user.setLogin(userRequestDTO.getLogin());
    }
    if (userRequestDTO.getPassword() != null) {
      user.setEncryptedPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
      user.setTokenSalt(JwtService.generateTokenSalt());
    }
    if (userRequestDTO.getName() != null) {
      user.setName(userRequestDTO.getName());
    }
    if (userRequestDTO.getRole() != null) {
      user.setRole(userRequestDTO.getRole());
    }
    return modelMapper.map(userRepository.save(user), UserResponseDTO.class);
  }

  public List<UserResponseDTO> getAllUsers() {
    return userRepository.findAll()
        .stream()
        .map(user -> modelMapper.map(user, UserResponseDTO.class))
        .toList();
  }

  public String issueAuthToken(String login, String password) {
    User user = userRepository.findByLogin(login)
        .orElseThrow(() -> new BadUsernameOrLoginException("Invalid login or password"));
    if (passwordEncoder.matches(password, user.getEncryptedPassword())) {
      return jwtService.issueAuthToken(user);
    } else {
      throw new BadUsernameOrLoginException("Invalid login or password");
    }
  }

  public Optional<String> exchangeTokenToUserInfo(String token) {
    Long id = jwtService.getUserIdFromToken(token);
    if (id == null) {
      return Optional.empty();
    }
    Optional<User> user = userRepository.findById(id);
    if (user.isEmpty() || jwtService.verifyAuthToken(token, user.get())) {
      return Optional.empty();
    }
    return Optional.of(jwtService.issueUserDataToken(user.get()));
  }
}
