package ru.netology.cloudstorage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "files")
public class FileEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  @OneToOne
  private UserEntity user;
  private String hash;
  private String file;
  private long size;

  public FileEntity(UserEntity user, String hash, String file, long size) {
    this.user = user;
    this.hash = hash;
    this.file = file;
    this.size = size;
  }
}
