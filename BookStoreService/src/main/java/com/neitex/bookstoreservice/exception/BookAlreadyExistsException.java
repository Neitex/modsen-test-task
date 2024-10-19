package com.neitex.bookstoreservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BookAlreadyExistsException extends ResponseStatusException {
  public BookAlreadyExistsException(String message) {
    super(HttpStatus.CONFLICT, message);
  }
}
