package ru.netology.cloudstorage.dto;

import lombok.Data;

@Data
public class LoginDTO {
  private String login;
  private String password;
}