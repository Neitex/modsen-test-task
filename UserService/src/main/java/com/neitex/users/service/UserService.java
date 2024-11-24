package com.neitex.users.service;

import com.neitex.users.dto.UserRequestDTO;
import com.neitex.users.dto.UserResponseDTO;
import com.neitex.users.exception.BadLoginCredentials;
import com.neitex.users.exception.NoSuchUserException;
import com.neitex.users.exception.UsernameTakenException;
import com.neitex.users.model.User;
import com.neitex.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Transactional
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final ModelMapper modelMapper;
  private final JwtService jwtService;

  public UserResponseDTO getUserById(@NonNull Long id) {
    return userRepository.findById(id).map(user -> modelMapper.map(user, UserResponseDTO.class))
        .orElseThrow(() -> new NoSuchUserException("User not found"));
  }

  public UserResponseDTO getUserByLogin(@NonNull String login) {
    return userRepository.findByLogin(login)
        .map(user -> modelMapper.map(user, UserResponseDTO.class))
        .orElseThrow(() -> new NoSuchUserException("User not found"));
  }


  public UserResponseDTO createUser(@NonNull UserRequestDTO userRequestDTO) {
    Objects.requireNonNull(userRequestDTO.getLogin(), "Login cannot be null");
    Objects.requireNonNull(userRequestDTO.getPassword(), "Password cannot be null");
    User user = modelMapper.map(userRequestDTO, User.class);
    if (userRepository.findByLogin(userRequestDTO.getLogin()).isPresent()) {
      throw new UsernameTakenException("User with such login already exists");
    }
    user.setEncryptedPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
    user.setTokenSalt(JwtService.generateTokenSalt());
    return modelMapper.map(userRepository.save(user), UserResponseDTO.class);
  }

  public void deleteUser(@NonNull Long id) {
    userRepository.deleteById(id);
  }


  public UserResponseDTO updateUser(@NonNull Long id, @NonNull UserRequestDTO userRequestDTO) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("User not found"));
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
    return userRepository.findAll().stream()
        .map(user -> modelMapper.map(user, UserResponseDTO.class)).toList();
  }

  public String issueAuthToken(@NonNull String login, @NonNull String password) {
    User user = userRepository.findByLogin(login)
        .orElseThrow(() -> new BadLoginCredentials("Invalid login or password"));
    if (passwordEncoder.matches(password, user.getEncryptedPassword())) {
      return jwtService.issueAuthToken(user);
    } else {
      throw new BadLoginCredentials("Invalid login or password");
    }
  }

  public Optional<String> exchangeTokenToUserInfo(@NonNull String token) {
    Long id = jwtService.getUserIdFromToken(token);
    if (id == null) {
      return Optional.empty();
    }
    Optional<User> user = userRepository.findById(id);
    if (user.isEmpty() || !jwtService.verifyAuthToken(token, user.get())) {
      return Optional.empty();
    }
    return Optional.of(jwtService.issueUserDataToken(user.get()));
  }
}
