package com.neitex.users.dto;

import com.neitex.users.model.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRequestDTO {
  private String login;
  private String password;
  private String name;
  private UserRole role;
}
