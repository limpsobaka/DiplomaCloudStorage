package ru.netology.cloudstorage.response;

import org.springframework.http.HttpStatus;
import ru.netology.cloudstorage.dto.ResponseDTO;

public class BadCredentialsResponse extends ResponseDTO {
  public BadCredentialsResponse() {
    super("Bad credentials", HttpStatus.BAD_REQUEST.value());
  }
  public BadCredentialsResponse(String message) {
    super(message, HttpStatus.BAD_REQUEST.value());
  }
}
