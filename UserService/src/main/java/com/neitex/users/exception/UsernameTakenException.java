package com.neitex.users.exception;

public class UsernameTakenException extends RuntimeException {

  public UsernameTakenException(String message) {
    super(message);
  }
}
