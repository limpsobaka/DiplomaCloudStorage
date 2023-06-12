package ru.netology.cloudstorage.response_exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BadCredentialsResponseException extends ResponseStatusException {
  public BadCredentialsResponseException() {
    super(HttpStatus.BAD_REQUEST, "Bad credentials");
  }
}
