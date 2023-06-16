package ru.netology.cloudstorage.controllers;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.netology.cloudstorage.service.AuthenticationService;
import ru.netology.cloudstorage.service.FileSystemStorageService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FileController.class)
class FileControllerTest {
  private MockMvc mockMvc;
  @MockBean
  FileSystemStorageService fileSystemStorageService;
  @MockBean
  AuthenticationService authenticationService;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new FileController(fileSystemStorageService, authenticationService)).build();
  }

  @Test
  void list() throws Exception {
    mockMvc.perform(get("/list")
                    .header("auth-token", "")
                    .queryParam("limit", "3")
            )
            .andExpect(status().isOk())
            .andExpect(content().string("[]"));
  }

  @Test
  void postFile() throws Exception {
    MockMultipartFile file
            = new MockMultipartFile(
            "file",
            "hello.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "12345".getBytes()
    );

    mockMvc.perform(multipart("/file").file(file)
                    .contentType("multipart/form-data")
                    .header("auth-token", "")
                    .param("filename", "new file.txt")
            )
            .andExpect(status().isOk())
            .andExpect(content().string(""));
  }

  @Test
  void deleteFile() throws Exception {
    mockMvc.perform(delete("/file")
                    .header("auth-token", "")
                    .param("filename", "new file.txt")
            )
            .andExpect(status().isOk())
            .andExpect(content().string(""));
  }

  @Test
  void getFile() throws Exception {
    mockMvc.perform(get("/file")
                    .header("auth-token", "")
                    .param("filename", "")
            )
            .andExpect(status().isOk())
            .andExpect(content().bytes("".getBytes()));
  }

  @Test
  void putFile() throws Exception {
    JSONObject responseJSON = new JSONObject();
    responseJSON.put("filename", "new file name.txt");

    mockMvc.perform(put("/file")
                    .header("auth-token", "")
                    .param("filename", "new file.txt")
                    .content(responseJSON.toString())
            )
            .andExpect(status().isOk())
            .andExpect(content().string(""));
  }
}