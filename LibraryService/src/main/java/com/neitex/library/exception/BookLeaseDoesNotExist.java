package com.neitex.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BookLeaseDoesNotExist extends ResponseStatusException {
  public BookLeaseDoesNotExist(String message) {
    super(HttpStatus.NOT_FOUND, message);
  }
}
