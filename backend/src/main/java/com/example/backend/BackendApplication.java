package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@CrossOrigin(origins = "http://localhost:5173")
	@GetMapping("/api/message")
	public String getMessage() {
		return "Hello from the backend! balbinka wita";
	}

	@CrossOrigin(origins = "http://localhost:5173")
	@GetMapping("/api/users/{id}")
	public User getUser(@PathVariable Long id) {
		 return new User(
            id,
            "Jan Kowalski",
            "kowal",
            "jan.kowalski@example.com"
        );
	}
}
