package ru.netology.cloudstorage.controllers;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.netology.cloudstorage.service.AuthenticationService;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AuthenticationController.class)
class AuthenticationControllerTest {
  private MockMvc mockMvc;
  @MockBean
  AuthenticationService authenticationService;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(new AuthenticationController(authenticationService)).build();
  }

  @Test
  void authenticateUser() throws Exception {
    JSONObject responseJSON = new JSONObject();
    responseJSON.put("login", "login");
    responseJSON.put("password", "password");

    mockMvc.perform(post("/login")
                    .accept(MediaType.parseMediaType("application/json"))
                    .contentType(MediaType.parseMediaType("application/json"))
                    .content(responseJSON.toString())
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(content().string(containsString("\"auth-token\"")));
  }


  @Test
  void logout() throws Exception {
    mockMvc.perform(post("/logout")
                    .header("auth-token", "")
            )
            .andExpect(status().isOk())
            .andExpect(content().string(""));
  }
}