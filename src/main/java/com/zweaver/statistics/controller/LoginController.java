package com.zweaver.statistics.controller;

import com.zweaver.statistics.config.BasicAuthConfig;
import com.zweaver.statistics.entity.UserEntity;
import com.zweaver.statistics.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/v1")
public class LoginController {

    /**
     * Checks if a raw string matches password criteria as follows:
     * minimum length of 8 characters,
     * starts with a letter
     * and contains at least one number or special character
     * @param password The raw String password to check criteria
     * @return A boolean indicating if a string matches the required criteria for a password.
     */
    private boolean matchPasswordCriteria(String password) {
        if (password.length() < 8) { return false; }
        if (!Character.isLetter(password.charAt(0))) { return false; }
        for (Character c : password.toCharArray()) {
            if (Character.isDigit(c)) { return true; }
        }
        
        // default catch-all case although technically unreachable
        return false;
    }
    
    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @PostMapping("/createUser")
    public ResponseEntity<String> createUser(
        @RequestHeader("username") String username, 
        @RequestHeader("password") String password
    ) {
        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            if (matchPasswordCriteria(password)) {
                userRepository.save(new UserEntity(username, passwordEncoder().encode(password)));
                return new ResponseEntity<>("User created successfully.", HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Please make sure your password is at least 8 characters long, starts with a letter and contains at least one number.", HttpStatus.BAD_REQUEST);
            }
        }
        
        return new ResponseEntity<>("User already exists.", HttpStatus.BAD_REQUEST);

    }

    @GetMapping("/login")
    public ResponseEntity<String> login(
        @RequestHeader("username") String username,
        @RequestHeader("password") String password
    ) {
        UserEntity user = userRepository.findByUsername(username);

        if (user != null && passwordEncoder().matches(password, user.getPassword())) {
            return new ResponseEntity<>("Logged in successfully.", HttpStatus.OK);
        }

        return new ResponseEntity<>("Username or password is incorrect.", HttpStatus.BAD_REQUEST);
    }
}
