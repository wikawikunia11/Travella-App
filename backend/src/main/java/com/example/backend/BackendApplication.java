package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

@SpringBootApplication
@RestController
public class BackendApplication {
	@Autowired
    private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@GetMapping("/api/message")
    public String getMessage() {
        return "Hello from the backend! balbinka wita";
    }

    @GetMapping("/api/users")
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/api/users/{id}")
    public User getUser(@PathVariable Long id) {
        //zabezpieczenie przed błędem 500
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.orElse(null);
    }

}
