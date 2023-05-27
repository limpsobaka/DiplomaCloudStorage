package ru.netology.cloudstorage.response;

import org.springframework.http.HttpStatus;
import ru.netology.cloudstorage.dto.ResponseDTO;

public class UnauthorizedResponse extends ResponseDTO {

  public UnauthorizedResponse() {
    super("Unauthorized error", HttpStatus.UNAUTHORIZED.value());
  }
}
