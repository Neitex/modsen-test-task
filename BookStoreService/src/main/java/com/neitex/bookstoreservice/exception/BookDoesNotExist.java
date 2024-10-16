package com.neitex.bookstoreservice.exception;

public class BookDoesNotExist extends RuntimeException {
  public BookDoesNotExist(String message) {
    super(message);
  }
}
