package com.neitex.userService.controller;

import com.neitex.userService.dto.UserRequestDTO;
import com.neitex.userService.dto.UserResponseDTO;
import com.neitex.userService.service.UserService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
  private final UserService userService;

  @GetMapping("/all")
  public List<UserResponseDTO> getAllUsers() {
    return userService.getAllUsers();
  }

  @GetMapping("/{id}")
  public UserResponseDTO getUserById(@PathVariable("id") Long id) {
    return userService.getUserById(id);
  }

  @GetMapping("/by-login/{login}")
  public UserResponseDTO getUserByLogin(@PathVariable("login") String login) {
    return userService.getUserByLogin(login);
  }

  @PostMapping("/{id}")
  public UserResponseDTO updateUser(@PathVariable("id") Long id, @RequestBody UserRequestDTO user) {
    return userService.updateUser(id, user);
  }

  @DeleteMapping("/{id}")
  public void deleteUser(@PathVariable("id") Long id) {
    userService.deleteUser(id);
  }

  @PutMapping
  public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO user) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user));
  }
}
