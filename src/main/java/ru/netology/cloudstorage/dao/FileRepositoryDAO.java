package ru.netology.cloudstorage.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloudstorage.entity.FileEntity;
import ru.netology.cloudstorage.entity.UserEntity;

import java.util.List;

@Repository
public interface FileRepositoryDAO extends JpaRepository<FileEntity, Long> {
  FileEntity findFileEntityByFileNameAndUser(String fileName, UserEntity userEntity);
  List<FileEntity> findFileEntitiesByUser(UserEntity userEntity, Pageable pageable);
  List<FileEntity> findFileEntitiesByHashAndUser(String hash, UserEntity userEntity);
}
