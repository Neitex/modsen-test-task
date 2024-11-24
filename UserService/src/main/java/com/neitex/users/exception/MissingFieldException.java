package com.neitex.users.exception;

public class MissingFieldException extends RuntimeException {

  public MissingFieldException(String message) {
    super(message);
  }
}
