package com.neitex.bookstoreservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AuthorDoesNotExist extends ResponseStatusException {

  public AuthorDoesNotExist(String message) {
    super(HttpStatus.NOT_FOUND, message);
  }
}
