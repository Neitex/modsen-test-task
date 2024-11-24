package com.neitex.library.exception;

public class BookLeaseAlreadyExistsException extends RuntimeException {

  public BookLeaseAlreadyExistsException(String message) {
    super(message);
  }
}
