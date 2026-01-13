package com.example.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.backend.model.LoginRequest;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword(passwordEncoder.encode("pass1"));
        user1.setName("First");
        user1.setSurname("User");
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword(passwordEncoder.encode("pass2"));
        user2.setName("Second");
        user2.setSurname("User");
        userRepository.save(user2);
    }

    //////////////// DATA ACCESS /////////////////

    @Test
    @WithMockUser
    public void shouldGetBackendMessage() throws Exception {
        mockMvc.perform(get("/api/message"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello from the backend! balbinka wita"));
    }

    @Test
    public void shouldNotAccessUsersWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void shouldAccessUsersWithAuth() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void shouldReturn404WhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/users/ghost_user"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldGetUserDataByUsername() throws Exception {
        mockMvc.perform(get("/api/users/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.name").value("First"))
                .andExpect(jsonPath("$.surname").value("User"));
    }

    @Test
    public void shouldDenyAccesToUserData() throws Exception {
        mockMvc.perform(get("/api/users/user1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldNotExposePasswordInJson() throws Exception {
        mockMvc.perform(get("/api/users/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    //////////////// REGISTRATION /////////////////

    @Test
    public void shouldRegisterUserSuccessfully() throws Exception {
        User newUser = new User();
        newUser.setUsername("user3");
        newUser.setPassword("pass3");
        newUser.setName("Jan");
        newUser.setSurname("Kowalski");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                // token should be created
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.username").value("user3"))
                // there should not be password in response
                .andExpect(jsonPath("$.user.password").doesNotExist());

        assertTrue(userRepository.findByUsername("user3").isPresent());
    }

    @Test
    public void shouldReturnErrorWhenRegisteringExistingUsername() throws Exception {
        User duplicateUser = new User();
        duplicateUser.setUsername("user1");
        duplicateUser.setPassword("pass3");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateUser)))
                .andExpect(status().isBadRequest());
    }

    //////////////// lOGIN /////////////////

    @Test
    public void shouldLoginSuccessfully() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setUsername("user1");
        login.setPassword("pass1");

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    public void shouldFailLoginWithWrongPassword() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setUsername("user1");
        login.setPassword("wrong_password");

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestWhenLoginBodyIsWrong() throws Exception {
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"invalid\": \"data\" }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFailLoginWithDifferentCaseUsername() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setUsername("USER1");
        login.setPassword("pass1");

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isBadRequest());
    }

    //////////////// DATA UPDATE /////////////////

    @Test
    public void shouldDenyUpdateWithoutAuth() throws Exception {
        User updatedData = new User();
        updatedData.setName("Name");
        updatedData.setSurname("Surname");

        mockMvc.perform(put("/api/users/user1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedData)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldUpdateOwnProfile() throws Exception {
        User updatedData = new User();
        updatedData.setName("Name");
        updatedData.setSurname("Surname");

        mockMvc.perform(put("/api/users/user1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedData)))
                .andExpect(status().isOk());

        User savedUser = userRepository.findByUsername("user1")
                .orElseThrow(() -> new AssertionError("User 'user1' not found in database!"));
        assertEquals("Name", savedUser.getName());
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldNotUpdateOtherUserProfile() throws Exception {
        User updatedData = new User();
        updatedData.setName("Name");
        updatedData.setSurname("Surname");

        mockMvc.perform(put("/api/users/user2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedData)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldKeepPasswordIntactAfterNameUpdate() throws Exception {
        User updatedData = new User();
        updatedData.setName("Name");
        updatedData.setSurname("Surname");

        mockMvc.perform(put("/api/users/user1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedData)))
                .andExpect(status().isOk());

        User savedUser = userRepository.findByUsername("user1").get();
        assertTrue(passwordEncoder.matches("pass1", savedUser.getPassword()));
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldKeepCreationDateIntactAfterNameUpdate() throws Exception {
        User oldUser = userRepository.findByUsername("user1").get();

        User updatedData = new User();
        updatedData.setName("Name");
        updatedData.setSurname("Surname");

        mockMvc.perform(put("/api/users/user1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedData)))
                .andExpect(status().isOk());

        User savedUser = userRepository.findByUsername("user1").get();
        assertEquals(oldUser.getCreationDate(), savedUser.getCreationDate());
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldAllowClearingBiography() throws Exception {
        User updatedData = new User();
        updatedData.setName("First");
        updatedData.setSurname("User");
        updatedData.setBiography(null);

        mockMvc.perform(put("/api/users/user1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.biography").isEmpty());
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldNotChangeNameToNull() throws Exception {
        User oldUser = userRepository.findByUsername("user1").get();

        User updatedData = new User();
        updatedData.setName(null);
        updatedData.setSurname("User");

        mockMvc.perform(put("/api/users/user1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").isNotEmpty());

        User savedUser = userRepository.findByUsername("user1").get();
        assertEquals(oldUser.getName(), savedUser.getName());
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldNotChangeSurnameToNull() throws Exception {
        User oldUser = userRepository.findByUsername("user1").get();

        User updatedData = new User();
        updatedData.setName("Name");
        updatedData.setSurname(null);

        mockMvc.perform(put("/api/users/user1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.surname").isNotEmpty());

        User savedUser = userRepository.findByUsername("user1").get();
        assertEquals(oldUser.getSurname(), savedUser.getSurname());
    }


    //////////////// DELETE /////////////////

    @Test
    public void shouldNotDeleteUserWithoutAuth() throws Exception {
        mockMvc.perform(delete("/api/users/user1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldDeleteOwnAccountSuccessfully() throws Exception {
        assertTrue(userRepository.findByUsername("user1").isPresent());

        mockMvc.perform(delete("/api/users/user1"))
                .andExpect(status().isOk());

        assertTrue(userRepository.findByUsername("user1").isEmpty());
    }

    @Test
    @WithMockUser(username = "non_existant_user")
    public void shouldReturnForbiddenWhenDeletingNonExistentUser() throws Exception {
        mockMvc.perform(delete("/api/users/non_existent_user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldNotDeleteOtherUserAccount() throws Exception {
        mockMvc.perform(delete("/api/users/user2"))
                .andExpect(status().isForbidden());

        assertTrue(userRepository.findByUsername("user2").isPresent());
    }
}