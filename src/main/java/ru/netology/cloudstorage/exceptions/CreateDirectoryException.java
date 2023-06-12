package ru.netology.cloudstorage.exceptions;

public class CreateDirectoryException extends RuntimeException {
  public CreateDirectoryException(String message, Throwable cause) {
    super(message, cause);
  }
}
