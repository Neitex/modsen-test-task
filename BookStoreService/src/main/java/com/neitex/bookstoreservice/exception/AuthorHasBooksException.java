package com.neitex.bookstoreservice.exception;

public class AuthorHasBooksException extends RuntimeException {

  public AuthorHasBooksException(String message) {
    super(message);
  }
}
