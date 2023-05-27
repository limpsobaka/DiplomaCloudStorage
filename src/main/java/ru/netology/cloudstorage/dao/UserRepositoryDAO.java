package ru.netology.cloudstorage.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloudstorage.entity.UserEntity;

@Repository
public interface UserRepositoryDAO extends JpaRepository<UserEntity, Long> {
  UserEntity findByLogin(String userName);

  UserEntity findUserByToken(String token);

}
