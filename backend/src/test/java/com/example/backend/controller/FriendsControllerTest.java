package com.example.backend.controller;

import com.example.backend.model.Post;
import com.example.backend.model.User;
import com.example.backend.model.Friendship;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.FriendshipRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class FriendsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void initialize() {
        postRepository.deleteAll();
        friendshipRepository.deleteAll();
        userRepository.deleteAll();

        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("pass1");
        user1.setName("First");
        user1.setSurname("User");
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("pass2");
        user2.setName("Second");
        user2.setSurname("User");
        userRepository.save(user2);

        Post friendPost = new Post();
        friendPost.setUser(user2);
        friendPost.setCaption("Hello from friend!");
        postRepository.save(friendPost);
    }

    ///////////// Friendship id order ///////////////

    @Test
    public void testFriendshipIdOrder() {
        Long id1 = 2222L;
        Long id2 = 1111L;
        Friendship f = new Friendship(id1,id2);
        assertEquals(id2, f.getFirstUserId());
        assertEquals(id1, f.getSecondUserId());
    }

    ///////////// Add friend //////////////

    @Test
    @WithMockUser(username = "user1")
    public void shouldAddFriendSuccessfully() throws Exception {
        mockMvc.perform(post("/api/users/user2/friends"))
                .andExpect(status().isOk())
                .andExpect(content().string("Added user2 to friends."));

        assertEquals(1, friendshipRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldNotAddHimself() throws Exception {
        mockMvc.perform(post("/api/users/user1/friends"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Can't add yourself to friends."));
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldNotAddSameFriendTwice() throws Exception {
        mockMvc.perform(post("/api/users/user2/friends"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/users/user2/friends"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Friendship with user2 already exists."));
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldNotAddNonExistentUser() throws Exception {
        mockMvc.perform(post("/api/users/notAUSer/friends"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found."));
    }

    @Test
    public void shouldNotAddFriendWithoutToken() throws Exception {
        mockMvc.perform(post("/api/users/user2/friends"))
                .andExpect(status().isForbidden());
    }

    /////////////// Remove friend ///////////////////

    @Test
    @WithMockUser(username = "user1")
    public void shouldRemoveFriendSuccessfully() throws Exception {
        mockMvc.perform(post("/api/users/user2/friends"))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/users/user2/friends"))
                .andExpect(status().isOk())
                .andExpect(content().string("Removed user2 from friends."));

        assertTrue(friendshipRepository.findAll().isEmpty());
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldNotRemoveNonExistentFriendship() throws Exception {
        mockMvc.perform(delete("/api/users/user2/friends"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Friendship does not exist."));
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldNotRemoveNonExistentUser() throws Exception {
        mockMvc.perform(delete("/api/users/notAUser/friends"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found."));
    }

    @Test
    public void shouldNotRemoveFriendWithoutToken() throws Exception {
        mockMvc.perform(delete("/api/users/user2/friends"))
                .andExpect(status().isForbidden());
    }

    /////////// Get friends list /////////////////

    @Test
    @WithMockUser(username = "user1")
    public void shouldGetFriendsList() throws Exception {
        mockMvc.perform(post("/api/users/user2/friends"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/user1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user2"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void shouldNotGetFriendsListWithoutToken() throws Exception {
        User u1 = userRepository.findByUsername("user1").orElseThrow();
        User u2 = userRepository.findByUsername("user2").orElseThrow();
        friendshipRepository.save(new Friendship(u1.getIdUser(), u2.getIdUser()));

        mockMvc.perform(get("/api/users/user1/friends"))
                .andExpect(status().isForbidden());
    }

    //////////////// Search users //////////////////////

    @Test
    @WithMockUser(username = "user1")
    public void shouldSearchUsersByQuery() throws Exception {
        mockMvc.perform(get("/api/users/search").param("query", "ser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("user2"))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldReturnEmptyListWhenNoUsersFound() throws Exception {
        mockMvc.perform(get("/api/users/search").param("query", "agata"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void shouldNotSearchUsersWithoutToken() throws Exception {
        mockMvc.perform(get("/api/users/search").param("query", "user"))
                .andExpect(status().isForbidden());
    }

    //////////////// Get friends posts /////////////////

    @Test
    @WithMockUser(username = "user1")
    public void shouldGetFriendsPostsSuccessfully() throws Exception {
        mockMvc.perform(get("/api/users/user1/friends-posts"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"))
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(post("/api/users/user2/friends"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/user1/friends-posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].caption").value("Hello from friend!"))
                .andExpect(jsonPath("$[0].user.username").value("user2"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldBlockAccessToOtherUsersPosts() throws Exception {
        mockMvc.perform(get("/api/users/user2/friends-posts"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldNotGetFriendsPostsWithoutToken() throws Exception {
        mockMvc.perform(get("/api/users/user1/friends-posts"))
                .andExpect(status().isForbidden());
    }
}
