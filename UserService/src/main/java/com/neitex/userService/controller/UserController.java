package com.neitex.userService.controller;

import com.neitex.userService.dto.UserRequestDTO;
import com.neitex.userService.dto.UserResponseDTO;
import com.neitex.userService.service.UserService;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/users")
@AllArgsConstructor
public class UserController {
  private final UserService userService;

  @GetMapping("/all")
  public List<UserResponseDTO> getAllUsers() {
    return userService.getAllUsers();
  }

  @GetMapping("/{id}")
  public UserResponseDTO getUserById(@PathVariable("id") Long id) {
    return userService.getUserById(id).orElseThrow(() -> new NoSuchElementException("User not found"));
  }

  @GetMapping("/by-login/{login}")
  public UserResponseDTO getUserByLogin(@PathVariable("login") String login) {
    return userService.getUserByLogin(login).orElseThrow(() -> new NoSuchElementException("User not found"));
  }

  @PostMapping("/{id}")
  public UserResponseDTO updateUser(@PathVariable("id") Long id, UserRequestDTO user) {
    return userService.updateUser(id, user);
  }

  @DeleteMapping("/{id}")
  public void deleteUser(@PathVariable("id") Long id) {
    userService.deleteUser(id);
  }

  @PutMapping("")
  public UserResponseDTO createUser(UserRequestDTO user) {
    return userService.createUser(user);
  }
}
