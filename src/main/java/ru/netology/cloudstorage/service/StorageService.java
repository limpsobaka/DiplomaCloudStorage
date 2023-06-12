package ru.netology.cloudstorage.service;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import ru.netology.cloudstorage.entity.FileEntity;
import ru.netology.cloudstorage.entity.UserEntity;

import java.util.List;

public interface StorageService {

  void saveFile(byte[] multipartFileByteArray, String filename, UserEntity userEntity);

  List<FileEntity> listAllFiles(UserEntity userEntity, Pageable page);

  Resource getFile(String filename, UserEntity userEntity);

  void deleteFile(String filename, UserEntity user);

  void changeFileName(UserEntity user, String filename, String newFileName);
}