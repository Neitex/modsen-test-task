package com.neitex.userService.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class BadUsernameOrLoginException extends ResponseStatusException {
  public BadUsernameOrLoginException(String message) {
    super(HttpStatusCode.valueOf(401), message);
  }
}
