package com.neitex.bookstoreservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AuthorHasBooksException extends ResponseStatusException {

  public AuthorHasBooksException(String message) {
    super(HttpStatus.CONFLICT, message);
  }
}
