package ru.netology.cloudstorage.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.dao.UserRepositoryDAO;
import ru.netology.cloudstorage.entity.UserEntity;

import java.util.UUID;

@Service
public class AuthenticationService {
  private final UserRepositoryDAO userRepositoryDAO;
  private final PasswordEncoder passwordEncoder;

  public AuthenticationService(UserRepositoryDAO userRepositoryDAO, PasswordEncoder passwordEncoder) {
    this.userRepositoryDAO = userRepositoryDAO;
    this.passwordEncoder = passwordEncoder;
  }

  public UserEntity authenticateUser(String login, String password) {
    UserEntity user = userRepositoryDAO.findByLogin(login);
    if (user != null && passwordEncoder.matches(password, user.getPassword())) {
      return user;
    } else {
      return null;
    }
  }

  public String generateAuthenticationToken(UserEntity user) {
    String token = UUID.randomUUID().toString();
    user.setToken(token);
    userRepositoryDAO.save(user);
    return token;
  }

  public boolean revokeAuthentication(String token) {
    UserEntity user = getUserByToken(token);
    if (user != null) {
      user.setToken(null);
      userRepositoryDAO.save(user);
      return true;
    } else {
      return false;
    }
  }

  public UserEntity getUserByToken(String token) {
    return userRepositoryDAO.findUserByToken(StringUtils.removeStart(token, "Bearer").trim());
  }
}
