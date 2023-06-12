package ru.netology.cloudstorage.response_exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BadRequestResponseException extends ResponseStatusException {
  public BadRequestResponseException(String reason) {
    super(HttpStatus.BAD_REQUEST, reason);
  }
}
