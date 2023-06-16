package ru.netology.cloudstorage.controllers;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.cloudstorage.dto.FileDTO;
import ru.netology.cloudstorage.dto.FileListDTO;
import ru.netology.cloudstorage.response_exceptions.BadRequestResponseException;
import ru.netology.cloudstorage.response_exceptions.ServerErrorResponseException;
import ru.netology.cloudstorage.service.AuthenticationService;
import ru.netology.cloudstorage.service.StorageService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/")
public class FileController {
  private final StorageService storageService;
  private final AuthenticationService authenticationService;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public FileController(StorageService storageService, AuthenticationService authenticationService) {
    this.storageService = storageService;
    this.authenticationService = authenticationService;
  }

  @GetMapping("list")
  public ResponseEntity<List<FileListDTO>> list(@RequestHeader("auth-token") String authToken, @RequestParam("limit") int limit) {
    authenticationService.checkUserTokenAuthentication(authToken);
    var userEntity = authenticationService.getUserByToken(authToken);

    Pageable pageSize = PageRequest.of(0, limit);
    List<FileListDTO> fileList = storageService.listAllFiles(userEntity, pageSize)
            .stream()
            .map(it -> new FileListDTO(it.getFileName(), it.getSize())).toList();

    return ResponseEntity.ok().body(fileList);
  }

  @PostMapping(path = "file", consumes = "multipart/form-data")
  public ResponseEntity<String> postFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename, @ModelAttribute FileDTO fileDTO) {
    authenticationService.checkUserTokenAuthentication(authToken);
    var userEntity = authenticationService.getUserByToken(authToken);

    byte[] multipartFileByteArray;

    try {
      multipartFileByteArray = fileDTO.getFile().getBytes();
    } catch (IOException e) {
      logger.error("Failed to store file {}", filename);
      throw new ServerErrorResponseException("Failed to store file " + filename);
    }

    storageService.saveFile(multipartFileByteArray, filename, userEntity);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("file")
  public ResponseEntity<String> deleteFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename) {
    authenticationService.checkUserTokenAuthentication(authToken);
    var userEntity = authenticationService.getUserByToken(authToken);

    storageService.deleteFile(filename, userEntity);
    return ResponseEntity.ok().build();
  }

  @GetMapping(value = "file", produces = "multipart/form-data")
  public ResponseEntity<Resource> getFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename) {
    authenticationService.checkUserTokenAuthentication(authToken);
    var userEntity = authenticationService.getUserByToken(authToken);
    var resource = storageService.getFile(filename, userEntity);

    return ResponseEntity.ok().body(resource);
  }

  @PutMapping("file")
  public ResponseEntity<String> putFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename, @RequestBody String inputNewFilename) {
    var newFileName = getNewFileName(inputNewFilename);

    authenticationService.checkUserTokenAuthentication(authToken);
    var userEntity = authenticationService.getUserByToken(authToken);

    storageService.changeFileName(userEntity, filename, newFileName);
    return ResponseEntity.ok().build();
  }

  private String getNewFileName(String inputNewFilename) {
    try {
      var jsonObject = new JSONObject(inputNewFilename);
      return jsonObject.getString("filename");
    } catch (JSONException e) {
      logger.error("Can not update file");
      throw new BadRequestResponseException("Can not update file");
    }
  }
}