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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.backend.model.UserResponse;
import com.example.backend.model.User;
import com.example.backend.service.UserService;
import com.example.backend.model.LoginRequest;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173"})
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/message")
    public String getMessage() {
        return "Hello from the backend! balbinka wita";
    }

    @GetMapping("/users")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<UserResponse> getUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
            .map(ResponseEntity::ok)
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
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

}
