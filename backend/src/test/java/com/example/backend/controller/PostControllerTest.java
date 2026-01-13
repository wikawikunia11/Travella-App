package com.example.backend.controller;

import com.example.backend.model.Post;
import com.example.backend.model.User;
import com.example.backend.repository.PostImageRepository;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;

import com.example.backend.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostImageRepository postImageRepository;

    @Autowired
    private PostService postService;

    @BeforeEach
    void initialize() {
        postImageRepository.deleteAll();
        postRepository.deleteAll();
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

        Post post = new Post();
        post.setCaption("User 2 post");
        post.setDescription("test post");
        post.setLatitude(52.2297);
        post.setLongitude(19.9450);
        post.setVisitDate(LocalDate.parse("2026-01-08"));
        post.setUser(user2);
        postRepository.save(post);
    }

    ///////////// Add post //////////////

    @Test
    @WithMockUser(username = "user1")
    public void shouldAddPostSuccessfully() throws Exception {
        MockMultipartFile image1 = new MockMultipartFile(
                "images",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
        mockMvc.perform(multipart("/api/posts/all")
                        .file(image1)
                        .param("caption", "Caption")
                        .param("description", "Just memories")
                        .param("latitude", "52.2297")
                        .param("longitude", "21.0122")
                        .param("visitDate", "2026-01-09"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.caption").value("Caption"))
                .andExpect(jsonPath("$.description").value("Just memories"))
                .andExpect(jsonPath("$.latitude").value(52.2297))
                .andExpect(jsonPath("$.longitude").value(21.0122))
                .andExpect(jsonPath("$.visitDate").value("2026-01-09"));

        assertEquals(2, postRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldAddPostWithoutPhotoSuccessfully() throws Exception {
        mockMvc.perform(multipart("/api/posts/all")
                    .param("caption", "Caption")
                    .param("description", "Just memories")
                    .param("latitude", "52.2297")
                    .param("longitude", "21.0122")
                    .param("visitDate", "2026-01-09"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.caption").value("Caption"));

        assertEquals(2, postRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldCreatePostWithMultipleImages() throws Exception {
        MockMultipartFile image1 = new MockMultipartFile(
                "images",
                "image1.jpg",
                "image/jpeg",
                "first image".getBytes()
        );

        MockMultipartFile image2 = new MockMultipartFile(
                "images",
                "image2.jpg",
                "image/jpeg",
                "second image".getBytes()
        );

        mockMvc.perform(multipart("/api/posts/all")
                        .file(image1)
                        .file(image2)
                        .param("caption", "Multiple photos")
                        .param("description", "Gallery from trip")
                        .param("latitude", "50.0647")
                        .param("longitude", "19.9450")
                        .param("visitDate", "2026-01-08"))
                .andExpect(status().isCreated());

        assertEquals(2, postRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldReturnErrorForInvalidDateFormat() throws Exception {
        mockMvc.perform(multipart("/api/posts/all")
                        .param("caption", "Test")
                        .param("description", "Test description")
                        .param("latitude", "50.0")
                        .param("longitude", "20.0")
                        .param("visitDate", "invalid-date"))
                .andExpect(status().isInternalServerError());

        assertEquals(1, postRepository.findAll().size());
    }

    @Test
    public void shouldReturnErrorForEmptyUser() throws Exception {
        mockMvc.perform(multipart("/api/posts/all")
                        .param("caption", "Test")
                        .param("description", "Test description")
                        .param("latitude", "50.0")
                        .param("longitude", "20.0")
                        .param("visitDate", "2025-01-08"))
                .andExpect(status().isInternalServerError());

        assertEquals(1, postRepository.findAll().size());
    }

    /////////////// Remove post ///////////////////

    @Test
    @WithMockUser(username = "user2")
    public void shouldRemovePostSuccessfully() throws Exception {
        assertEquals(1, postRepository.findAll().size());

        Post post = postRepository.findAll().get(0);
        mockMvc.perform(delete("/api/posts/" + post.getIdPost()))
                .andExpect(status().isOk());

        assertEquals(0, postRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldNotRemoveOthersPosts() throws Exception {
        Post post = postRepository.findAll().get(0);
        mockMvc.perform(delete("/api/posts/" + post.getIdPost()))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Unauthorized to delete this post"));

        assertEquals(1, postRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldNotRemoveNonExistentPosts() throws Exception {
        mockMvc.perform(delete("/api/posts/999"))
                .andExpect(status().isInternalServerError());

        assertEquals(1, postRepository.findAll().size());
    }

    @Test
    public void shouldNotRemovePostWithoutToken() throws Exception {
        assertEquals(1, postRepository.findAll().size());

        mockMvc.perform(delete("/api/posts/1"))
                .andExpect(status().isForbidden());
    }

    /////////// Get all post list /////////////////

    @Test
    @WithMockUser(username = "user1")
    public void shouldGetPostsList() throws Exception {
        assertEquals(1, postRepository.findAll().size());

        mockMvc.perform(get("/api/posts/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].caption").value("User 2 post"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    /////////// Get user post list /////////////////

    @Test
    @WithMockUser(username = "user2")
    public void shouldGetUsersPosts() throws Exception {
        assertEquals(1, postRepository.findAll().size());

        mockMvc.perform(get("/api/posts/user/user2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].caption").value("User 2 post"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldGetOtherUsersPosts() throws Exception {
        assertEquals(1, postRepository.findAll().size());

        mockMvc.perform(get("/api/posts/user/user2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].caption").value("User 2 post"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void shouldNotGetUsersPostsWithoutToken() throws Exception {
        assertEquals(1, postRepository.findAll().size());

        mockMvc.perform(get("/api/posts/user/user2"))
                .andExpect(status().isForbidden());
    }

    //////////////// Get all post photos //////////////////////

    @Test
    @WithMockUser(username = "user1")
    public void shouldGetPostImagesSuccessfully() throws Exception {
        MockMultipartFile image1 = new MockMultipartFile(
                "images",
                "image1.jpg",
                "image/jpeg",
                "first image".getBytes()
        );

        mockMvc.perform(multipart("/api/posts/all")
                        .file(image1)
                        .param("caption", "Multiple photos")
                        .param("description", "Gallery from trip")
                        .param("latitude", "50.0647")
                        .param("longitude", "19.9450")
                        .param("visitDate", "2026-01-08"))
                .andExpect(status().isCreated());

        Post post = postRepository.findAll().get(1);

        mockMvc.perform(get("/api/posts/"+post.getIdPost()+"/images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldGetEmptyPostImagesSuccessfully() throws Exception {
        Post post = postRepository.findAll().get(0);

        mockMvc.perform(get("/api/posts/"+post.getIdPost()+"/images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldReturnNotFoundForNonExistentPostImages() throws Exception {
        mockMvc.perform(get("/api/posts/999/images"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotGetPostImagesWithoutToken() throws Exception {
        mockMvc.perform(get("/api/posts/1/images"))
                .andExpect(status().isForbidden());
    }

    //////////////// Get single post image filename //////////////////////

    @Test
    @WithMockUser(username = "user1")
    public void shouldGetImageFileSuccessfully() throws Exception {
        MockMultipartFile image1 = new MockMultipartFile(
                "images",
                "image1.jpg",
                "image/jpeg",
                "first image".getBytes()
        );

        mockMvc.perform(multipart("/api/posts/all")
                        .file(image1)
                        .param("caption", "Multiple photos")
                        .param("description", "Gallery from trip")
                        .param("latitude", "50.0647")
                        .param("longitude", "19.9450")
                        .param("visitDate", "2026-01-08"))
                .andExpect(status().isCreated());
        Post createdPost = postRepository.findAll().get(1);
        List<String> images = postService.getPostImages(createdPost.getIdPost());

        mockMvc.perform(get(images.getFirst()))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.CONTENT_TYPE));
    }

    @Test
    @WithMockUser(username = "user2")
    public void shouldReturnNotFoundForNonExistentImageFile() throws Exception {
        mockMvc.perform(get("/api/posts/1/images/nonexistent.jpg"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotGetImageFileWithoutToken() throws Exception {
        mockMvc.perform(get("/api/posts/1/images/test.jpg"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user1")
    public void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/posts/user/nonexistent_user"))
                .andExpect(status().isNotFound());
    }
}
