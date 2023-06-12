package ru.netology.cloudstorage.response_exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ServerErrorResponseException extends ResponseStatusException {
  public ServerErrorResponseException(String reason) {
    super(HttpStatus.INTERNAL_SERVER_ERROR, reason);
  }
}
