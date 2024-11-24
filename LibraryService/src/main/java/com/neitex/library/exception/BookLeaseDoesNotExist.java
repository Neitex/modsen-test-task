package com.neitex.library.exception;

public class BookLeaseDoesNotExist extends RuntimeException {

  public BookLeaseDoesNotExist(String message) {
    super(message);
  }
}
