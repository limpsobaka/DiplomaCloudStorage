package ru.netology.cloudstorage.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.netology.cloudstorage.dao.UserRepositoryDAO;
import ru.netology.cloudstorage.entity.UserEntity;
import ru.netology.cloudstorage.response_exceptions.UnauthorizedResponseException;

import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(OutputCaptureExtension.class)
class AuthenticationServiceTest {
  UserRepositoryDAO userRepositoryDAO;
  PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    userRepositoryDAO = Mockito.mock(UserRepositoryDAO.class);
    passwordEncoder = Mockito.spy(BCryptPasswordEncoder.class);
  }

  @Test
  void authenticateUser() {
    String login = "ya@mail.ru";
    String password = "qwerty";
    UserEntity userEntity = Mockito.spy(UserEntity.class);
    userEntity.setLogin(login);
    userEntity.setPassword(passwordEncoder.encode(password));
    AuthenticationService authenticationService = new AuthenticationService(userRepositoryDAO, passwordEncoder);
    when(userRepositoryDAO.findByLogin(login)).thenReturn(userEntity);

    UserEntity expected = userEntity;

    UserEntity result = authenticationService.authenticateUser(login, password);

    Assertions.assertEquals(expected, result);
  }

  @Test
  void setTokenToUser() {
    UserEntity userEntity = Mockito.mock(UserEntity.class);
    AuthenticationService authenticationService = new AuthenticationService(userRepositoryDAO, passwordEncoder);

    String expected = "^\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}$";

    String result = authenticationService.setTokenToUser(userEntity);

    Assertions.assertTrue(result.matches(expected));
  }

  @Test
  void revokeAuthentication(CapturedOutput output) {
    UserEntity userEntity = Mockito.spy(UserEntity.class);
    userEntity.setLogin("test");
    String token = UUID.randomUUID().toString();
    AuthenticationService authenticationService = new AuthenticationService(userRepositoryDAO, passwordEncoder);
    Mockito.when(authenticationService.getUserByToken(token)).thenReturn(userEntity);
    authenticationService.revokeAuthentication(token);

    String expected = "Authentication revoked for login: " + userEntity.getLogin();

    String result = output.getOut();

    Assertions.assertTrue(result.contains(expected));
  }

  @Test
  void getUserByToken() {
    UserEntity userEntity = Mockito.spy(UserEntity.class);
    String token = UUID.randomUUID().toString();
    AuthenticationService authenticationService = new AuthenticationService(userRepositoryDAO, passwordEncoder);
    Mockito.when(userRepositoryDAO.findUserByToken(token)).thenReturn(userEntity);

    UserEntity expected = userEntity;

    UserEntity result = authenticationService.getUserByToken(token);

    Assertions.assertEquals(result, expected);
  }

  @Test
  void checkUserTokenAuthentication() {
    UserEntity userEntity = Mockito.spy(UserEntity.class);
    String token = UUID.randomUUID().toString();
    AuthenticationService authenticationService = new AuthenticationService(userRepositoryDAO, passwordEncoder);
    Mockito.when(authenticationService.getUserByToken(token)).thenReturn(userEntity);
    authenticationService.checkUserTokenAuthentication(token);

    Assertions.assertDoesNotThrow(UnauthorizedResponseException::new);
  }
}