package com.neitex.users.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class LoginRequestDTO {
  @NonNull
  private String login;
  @NonNull
  private String password;
}
