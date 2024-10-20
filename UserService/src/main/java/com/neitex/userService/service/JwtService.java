package com.neitex.userService.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.neitex.userService.model.User;
import java.util.Date;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  /**
   * Expiration time for user-facing tokens in milliseconds (usually much longer than application tokens)
   */
  private final Long userTokenExpiration;
  /**
   * Expiration time for internal tokens in milliseconds (should be short-lived, like 2 minutes)
   */
  private final Long applicationTokenExpiration;

  private final Algorithm algorithm;

  public JwtService(@Value("${jwt.secret}") String secret,
      @Value("${jwt.expiration.user}") Long userTokenExpiration,
      @Value("${jwt.expiration.application}") Long applicationTokenExpiration) {
    this.userTokenExpiration = userTokenExpiration;
    this.applicationTokenExpiration = applicationTokenExpiration;
    this.algorithm = Algorithm.HMAC256(secret);
  }

  public String issueAuthToken(User user) {
    return JWT.create()
        .withSubject(user.getId().toString())
        .withExpiresAt(new Date(System.currentTimeMillis() + userTokenExpiration))
        .withAudience("bookstore")
        .withClaim("login", user.getLogin())
        .withClaim("s", user.getTokenSalt())
        .sign(algorithm);
  }

  public boolean verifyAuthToken(String token, User user) {
    try {
      JWT.require(algorithm)
          .withSubject(user.getId().toString())
          .withClaim("s", user.getTokenSalt())
          .withAudience("bookstore")
          .build()
          .verify(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public String issueUserDataToken(User user) {
    return JWT.create()
        .withSubject(user.getId().toString())
        .withExpiresAt(new Date(System.currentTimeMillis() + applicationTokenExpiration))
        .withAudience("bookstore")
        .withClaim("login", user.getLogin())
        .withClaim("name", user.getName())
        .withClaim("role", user.getRole().name())
        .sign(algorithm);
  }

  public Long getUserIdFromToken(String token) {
    try {
      return Long.parseLong(JWT.require(algorithm).build().verify(token).getSubject());
    } catch (Exception e) {
      return null;
    }
  }

  public static String generateTokenSalt() {
    return RandomStringUtils.randomAlphanumeric(16);
  }
}
