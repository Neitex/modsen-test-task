package com.neitex.userService.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class UsernameTakenException extends ResponseStatusException {

  public UsernameTakenException(String message) {
    super(HttpStatusCode.valueOf(409), message);
  }
}
