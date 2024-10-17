package com.neitex.userService.dto;

import com.neitex.userService.model.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class UserResponseDTO {
  @NonNull
  private Long id;
  private String name;
  @NonNull
  private String login;
  @NonNull
  private UserRole role;
}
