package ru.netology.cloudstorage.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Pageable;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import ru.netology.cloudstorage.config.StorageProperties;
import ru.netology.cloudstorage.dao.FileRepositoryDAO;
import ru.netology.cloudstorage.entity.FileEntity;
import ru.netology.cloudstorage.entity.UserEntity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(OutputCaptureExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileSystemStorageServiceTest {
  Path rootLocation;
  FileRepositoryDAO fileRepositoryDAO;
  StorageProperties properties;

  @BeforeEach
  void setUp() throws IOException {
    this.fileRepositoryDAO = Mockito.mock(FileRepositoryDAO.class);
    this.properties = Mockito.spy(StorageProperties.class);
    properties.setLocation("./tmp");
    rootLocation = Paths.get(properties.getLocation());
    Files.createDirectories(rootLocation);
    try (FileWriter fileWriter = new FileWriter(rootLocation.resolve("new file.txt").toFile())) {
      fileWriter.write("Hello");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @AfterAll
  void clean() throws IOException {
    FileUtils.deleteDirectory(new File(rootLocation.toUri()));
  }

  @Test
  void listAllFiles() {
    UserEntity userEntity = Mockito.mock(UserEntity.class);
    Pageable page = Mockito.mock(Pageable.class);
    List<FileEntity> fileEntityList = new ArrayList<>();
    FileSystemStorageService fileSystemStorageService = new FileSystemStorageService(properties, fileRepositoryDAO);
    when(fileRepositoryDAO.findFileEntitiesByUser(userEntity, page)).thenReturn(fileEntityList);

    var expected = fileEntityList;

    var result = fileSystemStorageService.listAllFiles(userEntity, page);

    assertEquals(expected, result);
  }

  @Test
  void getFile() throws MalformedURLException {
    var userEntity = Mockito.mock(UserEntity.class);
    var filename = "new file.txt";
    var fileSystemStorageService = new FileSystemStorageService(properties, fileRepositoryDAO);
    var fileEntity = Mockito.spy(FileEntity.class);
    fileEntity.setFileNameUUID(filename);
    Mockito.when(fileRepositoryDAO.findFileEntityByFileNameAndUser(filename, userEntity)).thenReturn(fileEntity);

    var expected = new UrlResource(rootLocation.resolve(filename).toUri());

    var result = fileSystemStorageService.getFile(filename, userEntity);

    assertEquals(expected, result);
  }

  @Test
  void saveFile(CapturedOutput output) {
    var userEntity = Mockito.mock(UserEntity.class);
    var filename = "new file.txt";
    var fileSystemStorageService = new FileSystemStorageService(properties, fileRepositoryDAO);
    var fileEntity = Mockito.spy(FileEntity.class);
    fileEntity.setFileNameUUID(filename);
    var multipartFileByteArray = "Hello".getBytes();

    String expected = "File " + filename + " saved successfully";

    fileSystemStorageService.saveFile(multipartFileByteArray, filename, userEntity);
    String result = output.getOut();

    Assertions.assertTrue(result.contains(expected));
  }

  @Test
  void changeFileName(CapturedOutput output) {
    var userEntity = Mockito.mock(UserEntity.class);
    var filename = "new file.txt";
    var newFileName = "new file2.txt";
    var fileSystemStorageService = new FileSystemStorageService(properties, fileRepositoryDAO);
    var fileEntity = Mockito.spy(FileEntity.class);
    fileEntity.setFileNameUUID(filename);
    Mockito.when(fileRepositoryDAO.findFileEntityByFileNameAndUser(filename, userEntity)).thenReturn(fileEntity);

    String expected = "File " + filename + " renamed successfully";

    fileSystemStorageService.changeFileName(userEntity, filename, newFileName);
    String result = output.getOut();

    Assertions.assertTrue(result.contains(expected));
  }

  @Test
  void deleteFile(CapturedOutput output) {
    var userEntity = Mockito.mock(UserEntity.class);
    var filename = "new file.txt";
    var fileSystemStorageService = new FileSystemStorageService(properties, fileRepositoryDAO);
    var fileEntity = Mockito.spy(FileEntity.class);
    fileEntity.setFileNameUUID(filename);
    Mockito.when(fileRepositoryDAO.findFileEntityByFileNameAndUser(filename, userEntity)).thenReturn(fileEntity);

    String expected = "File " + filename + " deleted successfully";

    fileSystemStorageService.deleteFile(filename, userEntity);
    String result = output.getOut();

    Assertions.assertTrue(result.contains(expected));
  }
}