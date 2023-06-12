package ru.netology.cloudstorage.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.cloudstorage.dto.LoginDTO;
import ru.netology.cloudstorage.service.AuthenticationService;

import java.util.LinkedHashMap;
import java.util.Map;


@RestController
@RequestMapping("/")
public class AuthenticationController {
  private final AuthenticationService authenticationService;

  public AuthenticationController(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  @PostMapping(value = "login", consumes = "application/json")
  public ResponseEntity<Map<String, String>> authenticateUser(@RequestBody LoginDTO loginDto) {
    Map<String, String> map = new LinkedHashMap<>();
    var userEntity = authenticationService.authenticateUser(loginDto.getLogin(), loginDto.getPassword());
    var token = authenticationService.setTokenToUser(userEntity);
    map.put("auth-token", token);
    return ResponseEntity.ok().body(map);
  }

  @PostMapping("logout")
  public ResponseEntity<String> logout(@RequestHeader("auth-token") String authToken) {
    authenticationService.revokeAuthentication(authToken);
    return ResponseEntity.ok().build();
  }
}