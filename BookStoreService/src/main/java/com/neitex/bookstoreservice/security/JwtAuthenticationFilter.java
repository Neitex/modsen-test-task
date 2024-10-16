package com.neitex.bookstoreservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  public static final String BEARER_PREFIX = "Bearer ";
  public static final String HEADER_NAME = "Authorization";
  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain
  ) throws ServletException, IOException {

    var authHeader = request.getHeader(HEADER_NAME);
    if (!StringUtils.hasText(authHeader) || !StringUtils.startsWithIgnoreCase(authHeader,
        BEARER_PREFIX)) {
      filterChain.doFilter(request, response);
      return;
    }

    var jwt = authHeader.substring(BEARER_PREFIX.length());

    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = jwtService.getUserDetails(jwt);

      // Если токен валиден, то аутентифицируем пользователя

      SecurityContext context = SecurityContextHolder.createEmptyContext();
      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
          userDetails,
          null,
          userDetails.getAuthorities()
      );
      context.setAuthentication(authToken);
      SecurityContextHolder.setContext(context);
    }
    filterChain.doFilter(request, response);
  }
}