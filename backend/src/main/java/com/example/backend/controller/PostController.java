package com.example.backend.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.model.Post;
import com.example.backend.service.PostService;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @GetMapping("/all")
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @PostMapping("/all")
    public ResponseEntity<Post> addPost(
            @RequestParam("caption") String caption,
            @RequestParam("description") String description,
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude,
            @RequestParam("visitDate") String visitDate,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            Principal principal
    ) {
        try {
            LocalDate visit = LocalDate.parse(visitDate);

            Post createdPost = postService.createPost(
                    caption, description, latitude, longitude, visit, images, principal.getName()
            );

            return new ResponseEntity<>(createdPost, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/{idPost}")
    public ResponseEntity<?> deletePost(@PathVariable Long idPost, Principal principal) {
            postService.deletePost(idPost, principal.getName());
            return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Post>> getUserPosts(@PathVariable String username) {
        try {
            List<Post> posts = postService.getPostsByUsername(username);
            return ResponseEntity.ok(posts);
        } catch (RuntimeException e) {
             return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // get mapping by post id

    // get mapping by list of users -> friends posts
}