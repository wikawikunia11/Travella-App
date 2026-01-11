package com.example.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.backend.model.Friendship;
import com.example.backend.model.User;
import com.example.backend.repository.FriendshipRepository;
import com.example.backend.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class FriendshipService {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;

    public List<User> getFriendsList(User user) {
        Long userId = user.getIdUser();
        List<Friendship> friendships = friendshipRepository.findByFirstUserIdOrSecondUserId(userId, userId);

        List<Long> friendIds = friendships.stream()
                .map(f -> f.getFirstUserId().equals(userId) ? f.getSecondUserId() : f.getFirstUserId())
                .toList();

        return userRepository.findAllById(friendIds);
    }

    public ResponseEntity<?> getFriendsByUsername(String username) {
        return userRepository.findByUsername(username)
            .<ResponseEntity<?>>map(user -> {

                List<User> friends = getFriendsList(user);
                return ResponseEntity.ok(friends);
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("User " + username + " does not exist."));
    }

    @Transactional
    public ResponseEntity<?> addFriend(String loggedInUsername, String friendUsername) {
        if (loggedInUsername.equals(friendUsername)) {
            return ResponseEntity.badRequest().body("Can't add yourself to friends.");
        }

        Optional<User> userOpt = userRepository.findByUsername(loggedInUsername);
        Optional<User> friendOpt = userRepository.findByUsername(friendUsername);

        if (userOpt.isEmpty() || friendOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        User user = userOpt.get();
        User friend = friendOpt.get();

        Friendship friendship = new Friendship(user.getIdUser(), friend.getIdUser());

        if (friendshipRepository.existsByFirstUserIdAndSecondUserId(friendship.getFirstUserId(), friendship.getSecondUserId())) {
            return ResponseEntity.badRequest().body("Friendship with " + friendUsername + " already exists.");
        }

        friendshipRepository.save(friendship);
        return ResponseEntity.ok("Added " + friendUsername + " to friends.");
    }

    @Transactional
    public ResponseEntity<?> removeFriend(String loggedInUsername, String friendUsername) {
        Optional<User> userOpt = userRepository.findByUsername(loggedInUsername);
        Optional<User> friendOpt = userRepository.findByUsername(friendUsername);

        if (userOpt.isEmpty() || friendOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        User user = userOpt.get();
        User friend = friendOpt.get();

        Friendship friendship = new Friendship(user.getIdUser(), friend.getIdUser());

        if (!friendshipRepository.existsByFirstUserIdAndSecondUserId(friendship.getFirstUserId(), friendship.getSecondUserId())) {
            return ResponseEntity.badRequest().body("Friendship does not exist.");
        }

        friendshipRepository.deleteByFirstUserIdAndSecondUserId(friendship.getFirstUserId(), friendship.getSecondUserId());
        return ResponseEntity.ok("Removed " + friendUsername + " from friends.");
    }
}
