package com.neitex.userService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.neitex.userService.model.User;
import com.neitex.userService.model.UserRole;
import com.neitex.userService.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class JwtServiceTest {

  @MockBean
  private User user;

  private JwtService jwtService;

  @BeforeEach
  void setUp() {
    jwtService = new JwtService("testSecret", 1000L);
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