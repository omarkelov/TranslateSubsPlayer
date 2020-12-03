package com.webserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The exception thrown when a movie is not found.
 *
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such movie.")
public class MovieNotFoundException extends Exception {

  public MovieNotFoundException() {}

  public MovieNotFoundException(String message) {
    super(message);
  }

  public MovieNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public MovieNotFoundException(Throwable cause) {
    super(cause);
  }

  public MovieNotFoundException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
