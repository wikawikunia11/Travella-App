package com.example.backend.service;

import com.example.backend.model.LoginRequest;
import com.example.backend.model.User;
import com.example.backend.model.UserResponse;
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

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(
            user.getUsername(),
            user.getName(),
            user.getSurname(),
            user.getBiography(),
            user.getProfilePic()
        );
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(UserService::toUserResponse)
            .toList();
    }

    public Optional<UserResponse> getUserByUsername(String username) {
    return userRepository.findByUsername(username)
        .map(UserService::toUserResponse);
    }

    public ResponseEntity<?> registerUser(User newUser) {
        if (newUser.getUsername() == null || newUser.getUsername().isBlank() ||
        newUser.getPassword() == null || newUser.getPassword().isBlank() ||
        newUser.getName() == null || newUser.getName().isBlank() ||
        newUser.getSurname() == null || newUser.getSurname().isBlank()) {
            return new ResponseEntity<>(
                "All fields are required.",
                HttpStatus.BAD_REQUEST
            );
        }

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

        UserResponse response = toUserResponse(savedUser);

        return new ResponseEntity<>(Map.of(
            "user", response,
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
                if (updatedUser.getName() != null) user.setName(updatedUser.getName());
                if (updatedUser.getSurname() != null) user.setSurname(updatedUser.getSurname());
                user.setBiography(updatedUser.getBiography());
                user.setProfilePic(updatedUser.getProfilePic());
                userRepository.save(user);

                UserResponse response = toUserResponse(user);

                return new ResponseEntity<>(response, HttpStatus.OK);
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist."));
    }

    public ResponseEntity<?> searchUsers(String query) {
        if (query == null || query.isEmpty()) {
        return ResponseEntity.ok(List.of());
    }

    List<UserResponse> limitedResults = userRepository.findByUsernameContainingIgnoreCase(query)
            .stream()
            .limit(10)
            .map(UserService::toUserResponse)
            .toList();

    return ResponseEntity.ok(limitedResults);
    }

}
