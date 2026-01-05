package com.example.backend.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.backend.model.Post;
import com.example.backend.service.PostService;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @GetMapping("/all")
    public ResponseEntity<List<Post>> getAllPosts() {
        // List<PostDTO> - tylko to co potrzebne
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
}

    @PostMapping("/all")
    public ResponseEntity<Post> addPost(@RequestBody Post post, Principal principal) {
        // principal.getName() -> get username from token
        Post createdPost = postService.createPost(post, principal.getName());
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
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
            // map postDTO
            return ResponseEntity.ok(posts);
        } catch (RuntimeException e) {
             return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // get mapping by post id

    // get mapping by list of users -> friends posts
}