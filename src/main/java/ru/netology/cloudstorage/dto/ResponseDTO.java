package ru.netology.cloudstorage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResponseDTO {
  private String message;
  private int id;
}
