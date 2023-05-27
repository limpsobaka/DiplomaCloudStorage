package ru.netology.cloudstorage.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
public class FileDTO implements Serializable {
  private String hash;
  private MultipartFile file;
}
