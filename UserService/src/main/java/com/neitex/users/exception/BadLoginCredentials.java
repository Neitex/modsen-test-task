package com.neitex.users.exception;

/**
 * Exception thrown when user tries to log in with bad credentials.
 */
public class BadLoginCredentials extends RuntimeException {

  public BadLoginCredentials(String message) {
    super(message);
  }
}
