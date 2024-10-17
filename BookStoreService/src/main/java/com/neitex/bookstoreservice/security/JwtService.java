package com.neitex.bookstoreservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.neitex.bookstoreservice.exception.BadJWTException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service public class JwtService {

  private final Algorithm algorithm;
  public JwtService(@Value("${jwt.secret}") String jwtSigningKey) {
    algorithm = Algorithm.HMAC256(jwtSigningKey);
  }

  public GlobalUserDetails getUserDetails(
      String token) { // kind of dirty, but I'm not sure book storage microservice should have users
    try {
      DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
      return new GlobalUserDetails(jwt.getClaim("login").asString(),
          jwt.getClaim("role").asString(),
          jwt.getClaim("name").asString(), jwt.getSubject());
    } catch (JWTVerificationException | IllegalArgumentException e) {
      throw new BadJWTException("Invalid token");
    }
  }
}
