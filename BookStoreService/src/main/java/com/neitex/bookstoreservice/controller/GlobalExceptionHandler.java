package com.neitex.bookstoreservice.controller;

import com.neitex.bookstoreservice.exception.AuthorAlreadyExistsException;
import com.neitex.bookstoreservice.exception.AuthorDoesNotExist;
import com.neitex.bookstoreservice.exception.AuthorHasBooksException;
import com.neitex.bookstoreservice.exception.BadJWTException;
import com.neitex.bookstoreservice.exception.BookAlreadyExistsException;
import com.neitex.bookstoreservice.exception.BookDoesNotExist;
import com.neitex.bookstoreservice.exception.MissingFieldException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AuthorAlreadyExistsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorMessage handleAuthorAlreadyExistsException(AuthorAlreadyExistsException e) {
    return new ErrorMessage("Author already exists", e.getMessage(), HttpStatus.CONFLICT.value());
  }

  @ExceptionHandler(AuthorDoesNotExist.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorMessage handleAuthorDoesNotExist(AuthorDoesNotExist e) {
    return new ErrorMessage("Author does not exist", e.getMessage(), HttpStatus.NOT_FOUND.value());
  }

  @ExceptionHandler(AuthorHasBooksException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorMessage handleAuthorHasBooksException(AuthorHasBooksException e) {
    return new ErrorMessage("Author has books", e.getMessage(), HttpStatus.CONFLICT.value());
  }

  @ExceptionHandler(BadJWTException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ErrorMessage handleBadJWTException(BadJWTException e) {
    return new ErrorMessage("Bad JWT", e.getMessage(), HttpStatus.UNAUTHORIZED.value());
  }

  @ExceptionHandler(BookAlreadyExistsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorMessage handleBookAlreadyExistsException(BookAlreadyExistsException e) {
    return new ErrorMessage("Book already exists", e.getMessage(), HttpStatus.CONFLICT.value());
  }

  @ExceptionHandler(BookDoesNotExist.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorMessage handleBookDoesNotExist(BookDoesNotExist e) {
    return new ErrorMessage("Book does not exist", e.getMessage(), HttpStatus.NOT_FOUND.value());
  }

  @ExceptionHandler(MissingFieldException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorMessage handleMissingFieldException(MissingFieldException e) {
    return new ErrorMessage("Missing field", e.getMessage(), HttpStatus.BAD_REQUEST.value());
  }

  public record ErrorMessage(String error, String message, int status) {

  }
}
