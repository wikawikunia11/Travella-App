package com.example.backend.service;

import com.example.backend.model.LoginRequest;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public ResponseEntity<?> registerUser(User newUser) {
        if (userRepository.findByUsername(newUser.getUsername()).isPresent()) {
            return new ResponseEntity<>(
                "User '" + newUser.getUsername() + "' already exists.",
                HttpStatus.BAD_REQUEST
            );
        }

        String encodedPassword = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(encodedPassword);
        User savedUser = userRepository.save(newUser);
        String token = jwtService.generateToken(savedUser);

        return new ResponseEntity<>(Map.of(
            "user", savedUser,
            "token", token
        ), HttpStatus.CREATED);
    }

    public ResponseEntity<?> deleteUser(String username) {
        return userRepository.findByUsername(username)
            // use Optional<User> and Lambda
            .map(user -> {
                userRepository.delete(user);
                return new ResponseEntity<>("User '" + username + "' deleted.", HttpStatus.OK);
            })
            .orElseGet(() -> new ResponseEntity<>("User '" + username + "' does not exist.", HttpStatus.NOT_FOUND));
    }

    public ResponseEntity<?> authenticate(LoginRequest loginRequest) {
    return userRepository.findByUsername(loginRequest.getUsername())
        .filter(user -> passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
        .map(user -> {
                String token = jwtService.generateToken(user);
                return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "token", token
                ));
            })
        .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Invalid username or password.")));
    }

    public ResponseEntity<?> updateUser(String username, User updatedUser, String loggedInUsername) {
        if (!username.equals(loggedInUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("You can only edit your own profile.");
            }
        return userRepository.findByUsername(username)
        // because we return 2 different types based on situation
            .<ResponseEntity<?>>map(user -> {
                user.setName(updatedUser.getName());
                user.setSurname(updatedUser.getSurname());
                user.setBiography(updatedUser.getBiography());
                user.setProfilePic(updatedUser.getProfilePic());
                userRepository.save(user);
                return new ResponseEntity<>(user, HttpStatus.OK);
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist."));
    }

}
