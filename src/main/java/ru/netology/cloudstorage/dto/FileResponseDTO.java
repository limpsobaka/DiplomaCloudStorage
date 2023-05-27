package ru.netology.cloudstorage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.io.Resource;

@AllArgsConstructor
@Data
public class FileResponseDTO {
  private String hash;
  private Resource resource;
}