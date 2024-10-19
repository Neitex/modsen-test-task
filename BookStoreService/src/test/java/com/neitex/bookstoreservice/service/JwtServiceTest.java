package com.neitex.bookstoreservice.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.neitex.bookstoreservice.exception.BadJWTException;
import com.neitex.bookstoreservice.security.GlobalUserDetails;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

  private JwtService jwtService;
  private String validToken;
  private String invalidToken;

  @BeforeEach
  void setUp() {
    jwtService = new JwtService("secret");
    validToken = JWT.create()
        .withClaim("login", "user")
        .withClaim("role", "ROLE_USER")
        .withClaim("name", "User Name")
        .withSubject("user")
        .sign(Algorithm.HMAC256("secret"));
    invalidToken = "invalid.token.here";
  }

  @Test
  void getUserDetailsReturnsGlobalUserDetailsForValidToken() {
    GlobalUserDetails userDetails = jwtService.getUserDetails(validToken);

    assertEquals("user", userDetails.getLogin());
    assertEquals("ROLE_USER", userDetails.getRole());
    assertEquals("User Name", userDetails.getName());
    assertEquals("user", userDetails.getUsername());
  }

  @Test
  void getUserDetailsThrowsBadJWTExceptionForInvalidToken() {
    assertThrows(BadJWTException.class, () -> jwtService.getUserDetails(invalidToken));
  }

  @Test
  void getUserDetailsThrowsBadJWTExceptionForExpiredToken() {
    String expiredToken = JWT.create()
        .withClaim("login", "user")
        .withClaim("role", "ROLE_USER")
        .withClaim("name", "User Name")
        .withSubject("user")
        .withExpiresAt(new Date(System.currentTimeMillis() - 1000))
        .sign(Algorithm.HMAC256("secret"));

    assertThrows(BadJWTException.class, () -> jwtService.getUserDetails(expiredToken));
  }

  @Test
  void getUserDetailsThrowsBadJWTExceptionForTokenWithInvalidSignature() {
    String tokenWithInvalidSignature = JWT.create()
        .withClaim("login", "user")
        .withClaim("role", "ROLE_USER")
        .withClaim("name", "User Name")
        .withSubject("user")
        .sign(Algorithm.HMAC256("wrong_secret"));

    assertThrows(BadJWTException.class, () -> jwtService.getUserDetails(tokenWithInvalidSignature));
  }
}
