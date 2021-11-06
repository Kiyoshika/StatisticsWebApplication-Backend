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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/v1")
public class LoginController {
    
    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @PostMapping("/createUser")
    public ResponseEntity<String> createUser(
        @RequestParam("username") String username, 
        @RequestParam("password") String password
    ) {
        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            userRepository.save(new UserEntity(username, passwordEncoder().encode(password)));
            return new ResponseEntity<>("User created successfully.", HttpStatus.CREATED);
        }
        
        return new ResponseEntity<>("User already exists.", HttpStatus.BAD_REQUEST);

    }

    @GetMapping("/login")
    public ResponseEntity<String> login(
        @RequestParam("username") String username,
        @RequestParam("password") String password
    ) {
        UserEntity user = userRepository.findByUsername(username);

        if (user != null && passwordEncoder().matches(password, user.getPassword())) {
            return new ResponseEntity<>("Logged in successfully.", HttpStatus.OK);
        }

        return new ResponseEntity<>("Username or password is incorrect.", HttpStatus.BAD_REQUEST);
    }
}
