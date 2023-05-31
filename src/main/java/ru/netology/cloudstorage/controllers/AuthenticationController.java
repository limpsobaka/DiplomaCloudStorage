package ru.netology.cloudstorage.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.cloudstorage.dto.LoginDTO;
import ru.netology.cloudstorage.entity.UserEntity;
import ru.netology.cloudstorage.response.BadCredentialsResponse;
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
  public ResponseEntity authenticateUser(@RequestBody LoginDTO loginDto) {
    Map<String, String> map = new LinkedHashMap<>();
    String token;
    UserEntity user = authenticationService.authenticateUser(loginDto.getLogin(), loginDto.getPassword());
    if (user != null) {
      token = authenticationService.generateAuthenticationToken(user);
      map.put("auth-token", token);
      return new ResponseEntity(map, HttpStatus.OK);
    } else {
      return ResponseEntity.badRequest().body(new BadCredentialsResponse());
    }
  }

  @PostMapping("logout")
  public ResponseEntity logout(@RequestHeader("auth-token") String authToken) {
    if (authenticationService.revokeAuthentication(authToken)) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.badRequest().body(new BadCredentialsResponse("Bad token"));
    }
  }
}