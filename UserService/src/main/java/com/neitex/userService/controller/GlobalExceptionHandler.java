package com.neitex.userService.controller;

import com.neitex.userService.exception.BadLoginCredentials;
import com.neitex.userService.exception.MissingFieldException;
import com.neitex.userService.exception.NoSuchUserException;
import com.neitex.userService.exception.UsernameTakenException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UsernameTakenException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorMessage handleUsernameTakenException(UsernameTakenException e) {
    return new ErrorMessage("Username taken", e.getMessage(), HttpStatus.CONFLICT);
  }

  @ExceptionHandler(MissingFieldException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorMessage handleMissingFieldException(MissingFieldException e) {
    return new ErrorMessage("Missing required field", e.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NoSuchUserException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorMessage handleNoSuchUserException(NoSuchUserException e) {
    return new ErrorMessage("No such user was found", e.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(BadLoginCredentials.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ErrorMessage handleBadUsernameOrLoginException(BadLoginCredentials e) {
    return new ErrorMessage("Bad username or login", e.getMessage(), HttpStatus.UNAUTHORIZED);
  }

  public record ErrorMessage(String error, String message, HttpStatus status) {

  }
}
