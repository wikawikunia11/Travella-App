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

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
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
        .map(user -> ResponseEntity.ok("Login successful."))
        .orElseGet(() -> new ResponseEntity<>("Invalid username or password.", HttpStatus.BAD_REQUEST));
    }

    public ResponseEntity<?> updateUser(String username, User updatedUser) {
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
