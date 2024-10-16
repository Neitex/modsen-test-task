package com.neitex.bookstoreservice.exception;

public class AuthorAlreadyExistsException extends RuntimeException {
  public AuthorAlreadyExistsException(String message) {
    super(message);
  }
}
