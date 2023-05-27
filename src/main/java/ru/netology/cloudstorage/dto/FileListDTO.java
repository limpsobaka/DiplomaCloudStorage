package ru.netology.cloudstorage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class FileListDTO {
  private String filename;
  private long size;
}
