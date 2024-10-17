package com.neitex.userService.dto;

import com.neitex.userService.model.UserRole;
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
