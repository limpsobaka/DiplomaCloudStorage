package ru.netology.cloudstorage.response_exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnauthorizedResponseException extends ResponseStatusException {
  public UnauthorizedResponseException() {
    super(HttpStatus.UNAUTHORIZED, "Unauthorized error");
  }
}
