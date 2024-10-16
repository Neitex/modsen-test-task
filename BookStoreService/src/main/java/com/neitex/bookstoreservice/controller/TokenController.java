package com.neitex.bookstoreservice.controller;

import com.neitex.bookstoreservice.dto.AccessTokenResponseDTO;
import com.neitex.bookstoreservice.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/token")
@AllArgsConstructor
public class TokenController {
  private final JwtService jwtService;

  @GetMapping("/new")
  public AccessTokenResponseDTO issueToken() {
    return jwtService.generateToken();
  }
}
