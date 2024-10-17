package com.neitex.userService.dto;

import com.neitex.userService.model.UserRole;
import lombok.Data;

@Data
public class UserResponseDTO {
  private Long id;
  private String name;
  private String login;
  private UserRole role;
}
