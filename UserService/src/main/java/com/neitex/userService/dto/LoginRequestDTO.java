package com.neitex.userService.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class LoginRequestDTO {
  @NonNull
  private String login;
  @NonNull
  private String password;
}
