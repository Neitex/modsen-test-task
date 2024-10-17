package com.neitex.userService.controller;

import com.neitex.userService.dto.JwtResponseDTO;
import com.neitex.userService.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/auth")
@AllArgsConstructor
public class AuthController {
  private final UserService userService;

  @PostMapping("/login")
  public String login(String login, String password) {
    return new JwtResponseDTO(userService.issueAuthToken(login, password)).getToken();
  }
}
