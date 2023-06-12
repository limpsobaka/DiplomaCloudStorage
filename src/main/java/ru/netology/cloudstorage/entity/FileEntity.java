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
  private String fileName;
  private String fileNameUUID;
  private long size;

  public FileEntity(UserEntity user, String hash, String fileName, String fileNameUUID, long size) {
    this.user = user;
    this.hash = hash;
    this.fileName = fileName;
    this.fileNameUUID = fileNameUUID;
    this.size = size;
  }
}
