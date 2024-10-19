package com.neitex.bookstoreservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AuthorAlreadyExistsException extends ResponseStatusException {
  public AuthorAlreadyExistsException(String message) {
    super(HttpStatus.CONFLICT, message);
  }
}
