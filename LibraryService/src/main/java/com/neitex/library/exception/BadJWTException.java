package com.neitex.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BadJWTException extends ResponseStatusException {

  public BadJWTException(String message) {
    super(HttpStatus.UNAUTHORIZED, message);
  }
}
