package com.neitex.bookstoreservice.configuration;

import com.neitex.bookstoreservice.security.JwtAuthenticationFilter;
import jakarta.servlet.DispatcherType;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@AllArgsConstructor
public class SecurityConfiguration {

  private final JwtAuthenticationFilter JwtAuthenticationFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            authorizeRequests -> authorizeRequests.dispatcherTypeMatchers(DispatcherType.ERROR,
                    DispatcherType.INCLUDE, DispatcherType.ASYNC, DispatcherType.FORWARD)
                .permitAll()
                .requestMatchers("/**")
                .authenticated())
        .addFilterBefore(JwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .cors(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable);
    return http.build();
  }
}
