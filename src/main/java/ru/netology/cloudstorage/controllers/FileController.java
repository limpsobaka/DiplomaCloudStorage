package ru.netology.cloudstorage.controllers;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.netology.cloudstorage.dto.FileDTO;
import ru.netology.cloudstorage.dto.FileListDTO;
import ru.netology.cloudstorage.dto.ResponseDTO;
import ru.netology.cloudstorage.entity.UserEntity;
import ru.netology.cloudstorage.response.UnauthorizedResponse;
import ru.netology.cloudstorage.service.AuthenticationService;
import ru.netology.cloudstorage.service.StorageService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/")
public class FileController {
  private final StorageService storageService;
  private final AuthenticationService authenticationService;

  public FileController(StorageService storageService, AuthenticationService authenticationService) {
    this.storageService = storageService;
    this.authenticationService = authenticationService;
  }

  @GetMapping("list")
  public ResponseEntity list(@RequestHeader("auth-token") String authToken, @RequestParam("limit") int limit) {
    UserEntity user = authenticationService.getUserByToken(authToken);
    if (user != null) {
      Pageable pageSize = PageRequest.of(0, limit);
      List<FileListDTO> fileList = storageService.listAllFiles(user, pageSize)
              .stream()
              .map(it -> new FileListDTO(it.getFile(), it.getSize())).toList();
      if (fileList != null) {
        return ResponseEntity.ok().body(fileList);
      } else {
        return ResponseEntity.internalServerError()
                .body(new ResponseDTO("Error getting file list", HttpStatus.INTERNAL_SERVER_ERROR.value()));
      }
    } else {
      return new ResponseEntity(new UnauthorizedResponse(), HttpStatus.UNAUTHORIZED);
    }

  }

  @PostMapping(path = "file", consumes = "multipart/form-data")
  public ResponseEntity postFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename, @ModelAttribute FileDTO fileDTO) {
    UserEntity user = authenticationService.getUserByToken(authToken);
    if (user != null) {
      byte[] multipartFileByteArray;
      try {
        multipartFileByteArray = fileDTO.getFile().getBytes();
      } catch (IOException e) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to store file " + filename);
      }
      storageService.saveFile(multipartFileByteArray, filename, user);
      return ResponseEntity.ok().build();
    } else {
      return new ResponseEntity(new UnauthorizedResponse(), HttpStatus.UNAUTHORIZED);
    }
  }

  //
  @DeleteMapping("file")
  public ResponseEntity deleteFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename) throws IOException {
    UserEntity user = authenticationService.getUserByToken(authToken);
    if (user != null) {
      storageService.deleteFile(filename, user);
      return new ResponseEntity<>(HttpStatus.OK);
    } else {
      return new ResponseEntity(new UnauthorizedResponse(), HttpStatus.UNAUTHORIZED);
    }
  }

  @GetMapping(value = "file", produces = "multipart/form-data")
  public ResponseEntity getFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename) throws IOException {
    UserEntity user = authenticationService.getUserByToken(authToken);
    if (user != null) {
      Resource resource = storageService.getFileResource(filename, user);
      if (resource != null) {
        return ResponseEntity.ok().body(resource);
      } else {
        return ResponseEntity.internalServerError()
                .body(new ResponseDTO("Error upload file", HttpStatus.INTERNAL_SERVER_ERROR.value()));
      }
    } else {
      return new ResponseEntity(new UnauthorizedResponse(), HttpStatus.UNAUTHORIZED);
    }
  }

  @PutMapping("file")
  public ResponseEntity putFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename, @RequestBody String inputNewFilename) {
    String newFileName;
    try {
      JSONObject obj = new JSONObject(inputNewFilename);
      newFileName = obj.getString("filename");
    } catch (JSONException e) {
      return ResponseEntity.badRequest().body(new ResponseDTO("Can not update file", HttpStatus.BAD_REQUEST.value()));
    }
    UserEntity user = authenticationService.getUserByToken(authToken);
    if (user != null) {
      storageService.changeFileName(user, filename, newFileName);
      return new ResponseEntity(HttpStatus.OK);
    } else {
      return new ResponseEntity(new UnauthorizedResponse(), HttpStatus.UNAUTHORIZED);
    }
  }
}