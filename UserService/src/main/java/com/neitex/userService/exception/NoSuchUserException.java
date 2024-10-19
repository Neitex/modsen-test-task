package com.neitex.userService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoSuchUserException extends ResponseStatusException {
  public NoSuchUserException(String userNotFound) {
    super(HttpStatus.NOT_FOUND, userNotFound);
  }
}
