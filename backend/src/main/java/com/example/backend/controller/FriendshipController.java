package com.example.backend.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.backend.service.FriendshipService;
import com.example.backend.service.PostService;
import com.example.backend.service.UserService;

@RestController
@RequestMapping("/api")
public class FriendshipController {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private PostService postService;


    @GetMapping("/users/{username}/friends")
    public ResponseEntity<?> getFriendsByUsername(@PathVariable String username) {
        return friendshipService.getFriendsByUsername(username);
    }

    @PostMapping("/users/{friendUsername}/friends")
    public ResponseEntity<?> addFriend(@PathVariable String friendUsername, Principal principal) {
        return friendshipService.addFriend(principal.getName(), friendUsername);
    }

    @DeleteMapping("/users/{friendUsername}/friends")
    public ResponseEntity<?> removeFriend(@PathVariable String friendUsername, Principal principal) {
        return friendshipService.removeFriend(principal.getName(), friendUsername);
    }

    @GetMapping("/users/search")
    public ResponseEntity<?> searchUsers(@RequestParam String query) {
        return userService.searchUsers(query);
    }

    @GetMapping("/users/{username}/friends-posts")
    @PreAuthorize("#username == principal.username")
    public ResponseEntity<?> getFriendsPosts(@PathVariable String username, Principal principal) {
        return postService.getFriendsPosts(username);
    }
}
