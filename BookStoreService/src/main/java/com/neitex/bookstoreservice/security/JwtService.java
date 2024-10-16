package com.neitex.bookstoreservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.neitex.bookstoreservice.dto.AccessTokenResponseDTO;
import com.neitex.bookstoreservice.exception.BadJWTException;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service public class JwtService {
  private final byte[] jwtSigningKey;

  public JwtService(@Value("${token.signing.key}") String jwtSigningKey) {
    this.jwtSigningKey = Base64.getDecoder().decode(jwtSigningKey);
  }

  /**
   * Checks if the token is valid
   *
   * @param token JWT token
   * @return true if the token is valid, false otherwise
   */
  public boolean isTokenValid(String token) {
    Objects.requireNonNull(token, "Token cannot be null");
    try {
      DecodedJWT jwt = JWT.require(Algorithm.HMAC256(jwtSigningKey)).build().verify(token);
      return jwt.getExpiresAt().before(new Date());
    } catch (JWTVerificationException | IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * Generates a JWT token
   *
   * @return JWT token
   */
  public AccessTokenResponseDTO generateToken() {
    return new AccessTokenResponseDTO(JWT.create()
        .withIssuedAt(new Date(System.currentTimeMillis()))
        .withAudience("bookstore")
        .withExpiresAt(new Date(System.currentTimeMillis() + 100000 * 60 * 24))
        .sign(Algorithm.HMAC256(jwtSigningKey)));
  }

  /**
   * Returns user details from the token (only returns the audience as a username)
   *
   * @param token JWT token
   * @return UserDetails object
   * @throws BadJWTException if the token is invalid
   */
  public UserDetails getUserDetails(
      String token) { // kind of dirty, but I'm not sure book storage microservice should have users
    try {
      DecodedJWT jwt = JWT.decode(token);
      return new UserDetails() {
        @Override public Collection<? extends GrantedAuthority> getAuthorities() {
          return List.of();
        }

        @Override public String getPassword() {
          return "";
        }

        @Override public String getUsername() {
          return jwt.getAudience().get(0);
        }
      };
    } catch (JWTVerificationException | IllegalArgumentException e) {
      throw new BadJWTException("Invalid token");
    }
  }
}
