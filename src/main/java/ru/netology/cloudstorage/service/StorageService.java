package ru.netology.cloudstorage.service;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import ru.netology.cloudstorage.dto.FileDTO;
import ru.netology.cloudstorage.dto.FileResponseDTO;
import ru.netology.cloudstorage.entity.FileEntity;
import ru.netology.cloudstorage.entity.UserEntity;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface StorageService {

  void init();

  String saveFile(FileDTO fileDTO, String filename, UserEntity userEntity);

  List<FileEntity> listAllFiles(UserEntity userEntity, Pageable page);

  Path loadFilePath(String filename);

  Resource loadFileAsResource(String filename);

  FileResponseDTO getFile(String filename, UserEntity userEntity) throws IOException;

  void deleteFile(String filename, UserEntity user) throws IOException;

  void changeFileName(UserEntity user, String filename, String newFileName);
}