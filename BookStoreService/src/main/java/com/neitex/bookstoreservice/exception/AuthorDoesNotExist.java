package com.neitex.bookstoreservice.exception;

public class AuthorDoesNotExist extends RuntimeException {
  public AuthorDoesNotExist(String message) {
    super(message);
  }
}
