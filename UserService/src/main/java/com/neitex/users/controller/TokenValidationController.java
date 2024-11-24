package com.neitex.users.controller;

import com.neitex.users.dto.JwtDTO;
import com.neitex.users.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/validation")
@AllArgsConstructor
public class TokenValidationController {
  private final UserService userService;

  @PostMapping("/validate")
  public JwtDTO validateToken(@RequestBody JwtDTO token) {
    return new JwtDTO(userService.exchangeTokenToUserInfo(token.getToken()).orElse(null));
  }
}
