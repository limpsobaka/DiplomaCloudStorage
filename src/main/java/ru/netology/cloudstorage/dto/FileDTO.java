package ru.netology.cloudstorage.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileDTO {
  private String hash;
  private MultipartFile file;
}
