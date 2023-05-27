package ru.netology.cloudstorage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.netology.cloudstorage.config.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class CloudStorageApplication {

  public static void main(String[] args) {
    SpringApplication.run(CloudStorageApplication.class, args);
  }

}
