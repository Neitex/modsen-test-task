package com.neitex.library.controller;

import com.neitex.library.exception.BadJWTException;
import com.neitex.library.exception.BookLeaseAlreadyExistsException;
import com.neitex.library.exception.BookLeaseDoesNotExist;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BookLeaseDoesNotExist.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorMessage handleBookLeaseDoesNotExistException(BookLeaseDoesNotExist e) {
    return new ErrorMessage("Book lease does not exist", e.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(BookLeaseAlreadyExistsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorMessage handleBookLeaseAlreadyExistsException(BookLeaseAlreadyExistsException e) {
    return new ErrorMessage("Book lease already exists", e.getMessage(), HttpStatus.CONFLICT);
  }

  @ExceptionHandler(BadJWTException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ErrorMessage handleBadJWTException(BadJWTException e) {
    return new ErrorMessage("Bad JWT", e.getMessage(), HttpStatus.UNAUTHORIZED);
  }

  public record ErrorMessage(String error, String message, HttpStatus status) {

  }
}
