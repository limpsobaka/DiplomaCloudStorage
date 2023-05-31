package ru.netology.cloudstorage.service;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.netology.cloudstorage.config.StorageProperties;
import ru.netology.cloudstorage.dao.FileRepositoryDAO;
import ru.netology.cloudstorage.entity.FileEntity;
import ru.netology.cloudstorage.entity.UserEntity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class FileSystemStorageService implements StorageService {

  private final Path rootLocation;
  private final FileRepositoryDAO fileRepositoryDAO;

  public FileSystemStorageService(StorageProperties properties, FileRepositoryDAO fileRepositoryDAO) {
    this.rootLocation = Paths.get(properties.getLocation());
    this.fileRepositoryDAO = fileRepositoryDAO;
  }

  @Override
  @PostConstruct
  public void init() {
    try {
      Files.createDirectories(rootLocation);
    } catch (IOException e) {
      throw new RuntimeException("Could not initialize storage location", e);
    }
  }

  @Override
  public String saveFile(MultipartFile multipartFile, String filename, UserEntity user) {
    try {
      if (multipartFile.isEmpty()) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store empty file " + filename);
      }
      if (filename.contains("..")) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Cannot store file with relative path outside current directory " + filename);
      }
      try (InputStream inputStream = multipartFile.getInputStream()) {
        Files.copy(inputStream, this.rootLocation.resolve(filename),
                StandardCopyOption.REPLACE_EXISTING);
        String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(multipartFile.getInputStream());
        FileEntity file = new FileEntity(user, md5, filename, multipartFile.getSize());
        fileRepositoryDAO.save(file);

      }
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
              "Failed to store file " + filename);
    }
    return filename;
  }

  public List<FileEntity> listAllFiles(UserEntity userEntity, Pageable page) {
    return fileRepositoryDAO.findFilesByUser(userEntity, page);
  }

  @Override
  public Resource getFileResource(String filename, UserEntity user) {
    FileEntity file = fileRepositoryDAO.findFileByFileAndUser(filename, user);
    Resource resource = loadFileAsResource(file.getFile());
    return resource;
  }

  @Override
  public Resource loadFileAsResource(String filename) {
    try {
      Path file = loadFilePath(filename);
      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not read file: " + filename);
      }
    } catch (MalformedURLException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not read file: " + filename);
    }
  }

  @Override
  public Path loadFilePath(String filename) {
    return rootLocation.resolve(filename);
  }

  @Override
  public void deleteFile(String filename, UserEntity user) throws IOException {
    FileEntity file = fileRepositoryDAO.findFileByFileAndUser(filename, user);
    Files.delete(loadFilePath(file.getFile()));
    fileRepositoryDAO.delete(file);
  }

  @Override
  public void changeFileName(UserEntity user, String filename, String newFileName) {
    FileEntity file = fileRepositoryDAO.findFileByFileAndUser(filename, user);
    if (file != null) {
      loadFilePath(file.getFile()).toFile().renameTo(loadFilePath(newFileName).toFile());
      file.setFile(newFileName);
      fileRepositoryDAO.save(file);
    }
  }
}