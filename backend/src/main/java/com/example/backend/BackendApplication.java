package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.backend.LoginRequest;


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

    // czy wystawiac??
    @GetMapping("/api/users/{id}")
    public User getUser(@PathVariable Long id) {
        //error 500
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.orElse(null);
    }

    @GetMapping("/api/users/username/{username}")
    public User getUsername(@PathVariable String username) {
        //error 500
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElse(null);
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    //TODO
    //add post api endpoint to create new user (email drss) http 400 when email adress is already existed in db
    @PostMapping("/api/users")
    public ResponseEntity<?> createUser(@RequestBody User newUser) {
        // is username already in db
        if (userRepository.findByUsername(newUser.getUsername()).isPresent()) {
            // http 400 already exists
            return new ResponseEntity<>(
                "Użytkownik o nazwie '" + newUser.getUsername() + "' już istnieje.",
                HttpStatus.BAD_REQUEST
            );
        }
        // password encoding
        String encodedPassword = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(encodedPassword);
        User savedUser = userRepository.save(newUser);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    //add delete api endpoint to delete existing user (basis on email adress)
    @DeleteMapping("/api/users/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User userToDelete = userOptional.get();
            userRepository.delete(userToDelete);
            return new ResponseEntity<>(
                "Użytkownik o nazwie '" + username + "' został usunięty.",
                HttpStatus.OK
            );
        } else {
            return new ResponseEntity<>(
                "Użytkownik o nazwie '" + username + "' nie istnieje.",
                // 404 not found
                HttpStatus.NOT_FOUND
            );
        }
    }

    //update user Path mappring or put mapping -> check difference

    // login user based on username and password
    // http 400 - bad request
    // http 200 - ok
    @PostMapping("/api/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(
                "Nazwa użytkownika jest nieprawidłowa.",
                HttpStatus.BAD_REQUEST // 400 Bad Request
            );
        }

        User user = userOptional.get();
        boolean passwordMatches = passwordEncoder.matches(
            loginRequest.getPassword(), // raw data from frontend
            user.getPassword()          // hashed password from db
        );

        if (passwordMatches) {
            return new ResponseEntity<>(
                "Logowanie udane.",
                HttpStatus.OK // 200 OK
            );
        } else {
            return new ResponseEntity<>(
                "Hasło jest nieprawidłowe.",
                HttpStatus.BAD_REQUEST // 400 Bad Request
            );
        }
    }

    @PutMapping("/api/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>("User doesn't exist.", HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();

        user.setUsername(updatedUser.getUsername());
        user.setName(updatedUser.getName());
        user.setSurname(updatedUser.getSurname());
        user.setBiography(updatedUser.getBiography());
        user.setProfilePic(updatedUser.getProfilePic());

        userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
