package com.neitex.library.configuration;

import com.neitex.library.security.JwtAuthenticationFilter;
import jakarta.servlet.DispatcherType;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@AllArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/actuator/**").permitAll()
            .requestMatchers("/internal-books-lease/updates").permitAll()
            .dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.INCLUDE, DispatcherType.ASYNC,
                DispatcherType.FORWARD).permitAll().requestMatchers("/**").authenticated())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .cors(AbstractHttpConfigurer::disable).csrf(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable);
    return http.build();
  }
}
