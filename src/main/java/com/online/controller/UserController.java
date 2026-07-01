package com.online.controller;

import com.online.model.User;
import com.online.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// US-001: User Registration
// US-002: User Login
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody User user) {
        String message = userService.userRegistration(user);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        String message = userService.userLogin(user);
        if ("Invalid credentials".equals(message)) {
            return ResponseEntity.status(401).body(Map.of("message", message));
        }
        return ResponseEntity.ok(Map.of("message", message));
    }
}
