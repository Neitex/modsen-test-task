package com.neitex.bookstoreservice.exception;

public class BadJWTException extends RuntimeException {

  public BadJWTException(String message) {
    super(message);
  }
}
