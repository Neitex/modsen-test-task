package com.neitex.library.exception;

public class BadJWTException extends RuntimeException {
  public BadJWTException(String message) {
    super(message);
  }
}
