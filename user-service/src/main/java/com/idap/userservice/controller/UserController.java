package com.idap.userservice.controller;

import com.idap.userservice.dto.UserResponse;
import com.idap.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for user profile endpoints.
 * These endpoints require authentication (JWT token).
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Get current user's profile.
     * GET /api/users/me
     * 
     * The username is extracted from JWT by API Gateway and passed in X-User-Username header.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @RequestHeader(value = "X-User-Username", required = false) String username) {
        
        if (username == null || username.isBlank()) {
            log.warn("Missing X-User-Username header");
            return ResponseEntity.badRequest().build();
        }
        
        log.info("Getting profile for user: {}", username);
        UserResponse response = userService.getUserProfile(username);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user by ID.
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("Getting user by id: {}", id);
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

}
