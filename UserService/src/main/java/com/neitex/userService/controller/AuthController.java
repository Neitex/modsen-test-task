package com.neitex.userService.controller;

import com.neitex.userService.dto.JwtDTO;
import com.neitex.userService.dto.LoginRequestDTO;
import com.neitex.userService.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
  private final UserService userService;

  @PostMapping("/login")
  public JwtDTO login(@RequestBody LoginRequestDTO loginRequestDTO) {
    return new JwtDTO(userService.issueAuthToken(loginRequestDTO.getLogin(), loginRequestDTO.getPassword()));
  }
}
