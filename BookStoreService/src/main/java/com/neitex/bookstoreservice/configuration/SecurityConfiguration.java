package com.neitex.bookstoreservice.configuration;

import com.neitex.bookstoreservice.security.JwtAuthenticationFilter;
import jakarta.servlet.DispatcherType;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@AllArgsConstructor
public class SecurityConfiguration {
  private final JwtAuthenticationFilter JwtAuthenticationFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authorizeRequests ->
        authorizeRequests.requestMatchers("/**").authenticated()
            .dispatcherTypeMatchers(DispatcherType.ERROR)
            .permitAll()
            .requestMatchers("/token/issue").permitAll()
    ).addFilterBefore(JwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
