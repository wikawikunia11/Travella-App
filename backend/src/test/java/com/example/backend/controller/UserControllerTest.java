package com.example.backend.controller;

import com.example.backend.model.User;
import com.example.backend.service.FriendshipService;
import com.example.backend.service.JwtService;
import com.example.backend.service.PostService;
import com.example.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.AuthenticationProvider;

import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;


@WebMvcTest(UserController.class)
@AutoConfigureWebTestClient // web test client configuration
class UserControllerTest {

    @Autowired
	private WebTestClient webTestClient;

    @MockBean
    private UserService userService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private AuthenticationProvider authenticationProvider;

    @Test
    @WithMockUser // user simulation for authentication
	void greetingShouldReturnDefaultMessage() {
		webTestClient.get().uri("/api/message")
				.exchange()
				.expectBody(String.class)
				.isEqualTo("Hello from the backend! balbinka wita");
	}

    @Test
    void restrictedAccessToUsers() throws Exception {
        webTestClient.get().uri("/api/users")
                .exchange()
                .expectStatus().isUnauthorized();
    }


    @Test
    @WithMockUser
    void shouldReturnAllUsers() throws Exception {
        // --- GIVEN ---
        User user1 = new User();
        user1.setUsername("balbinka");
        User user2 = new User();
        user2.setUsername("janusz");
        List<User> allUsers = Arrays.asList(user1, user2);

        given(userService.getAllUsers()).willReturn(allUsers);

        // --- WHEN ---
        webTestClient.get().uri("/api/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()

        // --- THEN ---
                .expectStatus().isOk()
                .expectBodyList(User.class)
                    .hasSize(2)
                    // .contains, .jsonPath, .json
                    .contains(user1)
                    .contains(user2)
                    .value(users -> {
                        // BodyList = List<User>
                        assert users.get(0).getUsername().equals("balbinka");
                        assert users.get(1).getUsername().equals("janusz");
                    });
    }

}
