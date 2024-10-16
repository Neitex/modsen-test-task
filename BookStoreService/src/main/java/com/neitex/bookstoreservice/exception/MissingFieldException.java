package com.neitex.bookstoreservice.exception;

public class MissingFieldException extends RuntimeException {
  public MissingFieldException(String message) {
    super(message);
  }
}
