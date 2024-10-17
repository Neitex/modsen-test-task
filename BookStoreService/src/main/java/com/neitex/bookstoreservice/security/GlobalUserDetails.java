package com.neitex.bookstoreservice.security;

import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@AllArgsConstructor
public class GlobalUserDetails implements UserDetails {
  private final String login;
  private final String role;
  @Getter private final String name;
  @Getter private final String id;

  @Override public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_"+role));
  }

  @Override public String getPassword() {
    return null;
  }

  @Override public String getUsername() {
    return login;
  }
}
