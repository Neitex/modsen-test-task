package com.neitex.userService.controller;

import com.neitex.userService.dto.JwtResponseDTO;
import com.neitex.userService.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/validation")
@AllArgsConstructor
public class TokenValidationController {
  private final UserService userService;

  @PostMapping("/validate")
  public JwtResponseDTO validateToken(String token) {
    return new JwtResponseDTO(userService.exchangeTokenToUserInfo(token).orElse(null));
  }
}
