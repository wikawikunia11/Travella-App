package com.example.backend.controller;

import java.util.Optional;
import java.util.List;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import com.example.backend.model.User;
import com.example.backend.service.UserService;
import com.example.backend.service.FriendshipService;
import com.example.backend.model.LoginRequest;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173"})
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendshipService friendshipService;

    @GetMapping("/message")
    public String getMessage() {
        return "Hello from the backend! balbinka wita";
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{username}")
    public User getUsername(@PathVariable String username) {
        Optional<User> userOptional = userService.getUserByUsername(username);
        return userOptional.orElse(null);
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody User newUser) {
        return userService.registerUser(newUser);
    }

    @DeleteMapping("/users/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        return userService.deleteUser(username);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        return userService.authenticate(loginRequest);
    }

    @PutMapping("/users/{username}")
    public ResponseEntity<?> updateUser(
        @PathVariable String username,
        @RequestBody User updatedUser,
        Principal principal // get the currently logged-in user
    ) {
        return userService.updateUser(username, updatedUser, principal.getName());
    }

    @GetMapping("/users/{username}/friends")
    public ResponseEntity<?> getFriendsByUsername(@PathVariable String username) {
        return friendshipService.getFriendsByUsername(username);
    }

    @PostMapping("/users/{username}/friends")
    public ResponseEntity<?> addFriend(@PathVariable String username, Principal principal) {
        return friendshipService.addFriend(principal.getName(), username);
    }

    @DeleteMapping("/users/{username}/friends")
    public ResponseEntity<?> removeFriend(@PathVariable String username, Principal principal) {
        return friendshipService.removeFriend(principal.getName(), username);
}
}
