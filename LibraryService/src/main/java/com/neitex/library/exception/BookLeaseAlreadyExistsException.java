package com.neitex.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BookLeaseAlreadyExistsException extends ResponseStatusException {
  public BookLeaseAlreadyExistsException(String message) {
    super(HttpStatus.CONFLICT, message);
  }
}
