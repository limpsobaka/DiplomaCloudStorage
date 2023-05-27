package ru.netology.cloudstorage.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloudstorage.entity.FileEntity;
import ru.netology.cloudstorage.entity.UserEntity;

import java.util.List;

@Repository
public interface FileRepositoryDAO extends JpaRepository<FileEntity, Long> {
  FileEntity findFileByFileAndUser(String file, UserEntity userEntity);

  List<FileEntity> findFilesByUser(UserEntity userEntity, Pageable pageable);
}
