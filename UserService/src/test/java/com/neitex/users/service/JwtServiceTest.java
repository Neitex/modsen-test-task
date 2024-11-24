package com.neitex.users.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.neitex.users.model.User;
import com.neitex.users.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

  private User user;

  private JwtService jwtService;

  @BeforeEach
  void setUp() {
    user = mock(User.class);
    jwtService = new JwtService("testSecret", 1000L, 1000L);
  }

  @Test
  void issueAuthTokenReturnsValidToken() {
    when(user.getId()).thenReturn(1L);
    when(user.getLogin()).thenReturn("testLogin");
    when(user.getTokenSalt()).thenReturn("testSalt");

    String token = jwtService.issueAuthToken(user);

    assertNotNull(token);
  }

  @Test
  void verifyAuthTokenReturnsTrueForValidToken() {
    when(user.getId()).thenReturn(1L);
    when(user.getLogin()).thenReturn("testLogin");
    when(user.getTokenSalt()).thenReturn("testSalt");

    String token = jwtService.issueAuthToken(user);

    assertTrue(jwtService.verifyAuthToken(token, user));
  }

  @Test
  void verifyAuthTokenReturnsFalseForInvalidToken() {
    when(user.getId()).thenReturn(1L);
    when(user.getTokenSalt()).thenReturn("testSalt");

    String invalidToken = "invalidToken";

    assertFalse(jwtService.verifyAuthToken(invalidToken, user));
  }

  @Test
  void issueUserDataTokenReturnsValidToken() {
    when(user.getId()).thenReturn(1L);
    when(user.getLogin()).thenReturn("testLogin");
    when(user.getName()).thenReturn("testName");
    when(user.getRole()).thenReturn(UserRole.EDITOR);

    String token = jwtService.issueUserDataToken(user);

    assertNotNull(token);
  }

  @Test
  void getUserIdFromTokenReturnsCorrectId() {
    when(user.getId()).thenReturn(1L);
    when(user.getLogin()).thenReturn("testLogin");
    when(user.getTokenSalt()).thenReturn("testSalt");

    String token = jwtService.issueAuthToken(user);

    Long userId = jwtService.getUserIdFromToken(token);

    assertEquals(1L, userId);
  }

  @Test
  void getUserIdFromTokenReturnsNullForInvalidToken() {
    String invalidToken = "invalidToken";

    Long userId = jwtService.getUserIdFromToken(invalidToken);

    assertNull(userId);
  }

  @Test
  void generateTokenSaltReturnsNonNullSalt() {
    String salt = JwtService.generateTokenSalt();

    assertNotNull(salt);
    assertEquals(16, salt.length());
  }
}