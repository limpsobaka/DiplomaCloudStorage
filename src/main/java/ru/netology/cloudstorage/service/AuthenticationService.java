package ru.netology.cloudstorage.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.dao.UserRepositoryDAO;
import ru.netology.cloudstorage.entity.UserEntity;
import ru.netology.cloudstorage.response_exceptions.BadCredentialsResponseException;
import ru.netology.cloudstorage.response_exceptions.UnauthorizedResponseException;

import java.util.UUID;

@Service
public class AuthenticationService {
  private final UserRepositoryDAO userRepositoryDAO;
  private final PasswordEncoder passwordEncoder;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public AuthenticationService(UserRepositoryDAO userRepositoryDAO, PasswordEncoder passwordEncoder) {
    this.userRepositoryDAO = userRepositoryDAO;
    this.passwordEncoder = passwordEncoder;
  }

  public UserEntity authenticateUser(String login, String password) {
    var userEntity = userRepositoryDAO.findByLogin(login);
    if (userEntity != null && passwordEncoder.matches(password, userEntity.getPassword())) {
      logger.debug("Authentication successes for login {}", login);
      return userEntity;
    }
    logger.warn("Bad credentials for login {}", login);
    throw new BadCredentialsResponseException();
  }

  public String setTokenToUser(UserEntity userEntity) {
    var token = generateToken();
    userEntity.setToken(token);
    userRepositoryDAO.save(userEntity);
    logger.debug("Token assigned to login: {}", userEntity.getLogin());
    return token;
  }

  public void revokeAuthentication(String token) {
    var userEntity = getUserByToken(token);
    if (userEntity == null) {
      logger.warn("Bad token presented for revocation");
      throw new BadCredentialsResponseException();
    }
    userEntity.setToken(null);
    userRepositoryDAO.save(userEntity);
    logger.debug("Authentication revoked for login: {}", userEntity.getLogin());
  }

  public UserEntity getUserByToken(String token) {
    return userRepositoryDAO.findUserByToken(StringUtils.removeStart(token, "Bearer").trim());
  }

  public void checkUserTokenAuthentication(String authToken) {
    if (getUserByToken(authToken) == null) {
      logger.warn("Unauthorized request");
      throw new UnauthorizedResponseException();
    }
  }

  private String generateToken() {
    return UUID.randomUUID().toString();
  }
}
