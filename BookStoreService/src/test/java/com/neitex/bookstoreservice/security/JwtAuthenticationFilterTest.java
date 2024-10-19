package com.neitex.bookstoreservice.security;

import com.neitex.bookstoreservice.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

  private JwtAuthenticationFilter jwtAuthenticationFilter;
  private JwtService jwtService;
  private FilterChain filterChain;
  private HttpServletRequest request;
  private HttpServletResponse response;

  @BeforeEach
  void setUp() {
    jwtService = mock(JwtService.class);
    jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService);
    filterChain = mock(FilterChain.class);
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
  }

  @Test
  void doFilterInternal_NoAuthHeader_ShouldProceedWithoutAuthentication() throws ServletException, IOException {
    when(request.getHeader(JwtAuthenticationFilter.HEADER_NAME)).thenReturn(null);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // SecurityContextHolder should remain untouched, no authentication should be performed
    verify(filterChain, times(1)).doFilter(request, response);
    verify(jwtService, times(0)).getUserDetails(any());
  }

  @Test
  void doFilterInternal_InvalidAuthHeader_ShouldProceedWithoutAuthentication() throws ServletException, IOException {
    when(request.getHeader(JwtAuthenticationFilter.HEADER_NAME)).thenReturn("InvalidHeader");

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // SecurityContextHolder should remain untouched, no authentication should be performed
    verify(filterChain, times(1)).doFilter(request, response);
    verify(jwtService, times(0)).getUserDetails(any());
  }

  @Test
  void doFilterInternal_ValidJWT_UserAlreadyAuthenticated_ShouldNotAuthenticateAgain() throws ServletException, IOException {
    when(request.getHeader(JwtAuthenticationFilter.HEADER_NAME)).thenReturn("Bearer valid-jwt-token");

    // Simulate that there is already an authenticated user in SecurityContext
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("user", null, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // JwtService should not be called because the user is already authenticated
    verify(jwtService, times(0)).getUserDetails(any());
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  void doFilterInternal_ValidJWT_UserNotAuthenticated_ShouldAuthenticateUser() throws ServletException, IOException {
    when(request.getHeader(JwtAuthenticationFilter.HEADER_NAME)).thenReturn("Bearer valid-jwt-token");

    // Simulate no user authenticated in SecurityContext
    SecurityContextHolder.getContext().setAuthentication(null);

    GlobalUserDetails userDetails = mock(GlobalUserDetails.class);
    when(jwtService.getUserDetails("valid-jwt-token")).thenReturn(userDetails);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Verify that JwtService was called to get user details and user is authenticated
    verify(jwtService, times(1)).getUserDetails("valid-jwt-token");

    // SecurityContext should have the authenticated user now
    assert SecurityContextHolder.getContext().getAuthentication() != null;

    verify(filterChain, times(1)).doFilter(request, response);
  }
}
