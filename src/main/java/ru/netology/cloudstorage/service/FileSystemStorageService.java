package ru.netology.cloudstorage.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netology.cloudstorage.config.StorageProperties;
import ru.netology.cloudstorage.dao.FileRepositoryDAO;
import ru.netology.cloudstorage.entity.FileEntity;
import ru.netology.cloudstorage.entity.UserEntity;
import ru.netology.cloudstorage.exceptions.CreateDirectoryException;
import ru.netology.cloudstorage.response_exceptions.ServerErrorResponseException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class FileSystemStorageService implements StorageService {

  private final Path rootLocation;
  private final FileRepositoryDAO fileRepositoryDAO;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public FileSystemStorageService(StorageProperties properties, FileRepositoryDAO fileRepositoryDAO) {
    this.rootLocation = Paths.get(properties.getLocation());
    this.fileRepositoryDAO = fileRepositoryDAO;
  }

  @PostConstruct
  private void init() {
    try {
      Files.createDirectories(rootLocation);
    } catch (IOException e) {
      throw new CreateDirectoryException("Could not initialize storage location", e);
    }
  }

  @Override
  public List<FileEntity> listAllFiles(UserEntity userEntity, Pageable page) {
    logger.debug("File list returned successfully");
    return fileRepositoryDAO.findFileEntitiesByUser(userEntity, page);
  }

  @Override
  public Resource getFile(String filename, UserEntity user) {
    var fileEntity = fileRepositoryDAO.findFileEntityByFileNameAndUser(filename, user);
    var resource = loadFileAsResource(fileEntity.getFileNameUUID());
    logger.debug("File resource of {} returned successfully", filename);
    return resource;
  }

  @Override
  @Transactional(rollbackForClassName = "ServerErrorResponseException")
  public void saveFile(byte[] multipartFileByteArray, String filename, UserEntity userEntity) {
    checkFileLength(multipartFileByteArray, filename);
    checkFilePath(filename);
    checkFileDuplicationByHash(multipartFileByteArray, filename, userEntity);
    filename = isFileDuplicatedByFileName(filename, userEntity) ? generateNewFileName(filename) : filename;

    var fileNameUUID = getFileNameUUIDWithExtension(filename);
    try (FileOutputStream fileOutputStream = new FileOutputStream(this.rootLocation.resolve(fileNameUUID).toFile())) {
      var fileEntity = new FileEntity(userEntity,
              getMd5FileHash(multipartFileByteArray),
              filename,
              fileNameUUID,
              multipartFileByteArray.length
      );
      fileRepositoryDAO.save(fileEntity);
      fileOutputStream.write(multipartFileByteArray);
    } catch (IOException e) {
      logger.error("Failed to store file {}", filename);
      throw new ServerErrorResponseException("Failed to store file " + filename);
    }
    logger.debug("File {} saved successfully", filename);
  }

  @Override
  @Transactional(rollbackForClassName = "ServerErrorResponseException")
  public void deleteFile(String filename, UserEntity user) {
    var fileEntity = fileRepositoryDAO.findFileEntityByFileNameAndUser(filename, user);
    fileRepositoryDAO.delete(fileEntity);
    try {
      Files.delete(loadFilePath(fileEntity.getFileNameUUID()));
    } catch (IOException e) {
      logger.error("Failed to delete file {}", filename);
      throw new ServerErrorResponseException("Failed to delete file " + filename);
    }
    logger.debug("File {} deleted successfully", filename);
  }

  @Override
  public void changeFileName(UserEntity user, String filename, String newFileName) {
    var fileEntity = fileRepositoryDAO.findFileEntityByFileNameAndUser(filename, user);
    if (fileEntity == null) {
      logger.error("Failed to find file {}", filename);
      throw new ServerErrorResponseException("Failed to find file " + filename);
    }
    fileEntity.setFileName(newFileName);
    fileRepositoryDAO.save(fileEntity);
    logger.debug("File {} renamed successfully", filename);
  }

  private void checkFileLength(byte[] multipartFileByteArray, String filename) {
    if (multipartFileByteArray.length == 0) {
      logger.error("Failed to store empty file {}", filename);
      throw new ServerErrorResponseException("Failed to store empty file " + filename);
    }
  }

  private void checkFilePath(String filename) {
    if (filename.contains("..")) {
      logger.error("Cannot store file with path outside current directory {}", filename);
      throw new ServerErrorResponseException("Cannot store file with path outside current directory " + filename);
    }
  }

  private void checkFileDuplicationByHash(byte[] multipartFileByteArray, String filename, UserEntity userEntity) {
    if (!fileRepositoryDAO.findFileEntitiesByHashAndUser(getMd5FileHash(multipartFileByteArray), userEntity).isEmpty()) {
      logger.warn("File with same hash as file {} already in cloud", filename);
      throw new ServerErrorResponseException("File with same hash as file " + filename + " already in cloud");
    }
  }

  private boolean isFileDuplicatedByFileName(String fileName, UserEntity userEntity) {
    return fileRepositoryDAO.findFileEntityByFileNameAndUser(fileName, userEntity) != null;
  }

  private String generateNewFileName(String fileName) {
    var extension = fileName.substring(fileName.lastIndexOf("."));
    return fileName.replace(extension, "(copy)" + extension);
  }

  private Resource loadFileAsResource(String filename) {
    try {
      var filePath = loadFilePath(filename);
      var resource = new UrlResource(filePath.toUri());
      if (resource.exists() || resource.isReadable()) {
        logger.debug("File resource {} loaded successfully", filename);
        return resource;
      }
      logger.error("Could not read file: {}", filename);
      throw new ServerErrorResponseException("Could not read file: " + filename);
    } catch (MalformedURLException e) {
      logger.error("Could not read file: {}", filename);
      throw new ServerErrorResponseException("Could not read file: " + filename);
    }
  }

  private Path loadFilePath(String filename) {
    logger.debug("File path loaded successfully");
    return rootLocation.resolve(filename);
  }

  private String getMd5FileHash(byte[] multipartFileByteArray) {
    return org.apache.commons.codec.digest.DigestUtils.md5Hex(multipartFileByteArray);
  }

  private String getFileNameUUIDWithExtension(String fileName) {
    var extension = fileName.substring(fileName.lastIndexOf("."));
    return UUID.randomUUID() + extension;
  }
}