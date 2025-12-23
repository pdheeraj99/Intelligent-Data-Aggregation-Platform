package com.idap.userservice.service;

import com.idap.userservice.dto.AuthResponse;
import com.idap.userservice.dto.LoginRequest;
import com.idap.userservice.dto.RegisterRequest;
import com.idap.userservice.dto.UserResponse;
import com.idap.userservice.exception.UserAlreadyExistsException;
import com.idap.userservice.exception.UserNotFoundException;
import com.idap.userservice.model.Role;
import com.idap.userservice.model.User;
import com.idap.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for user management and authentication.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Register a new user.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        // Create new user
        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .roles(Set.of(Role.USER))
            .enabled(true)
            .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());

        // Generate JWT token
        String token = jwtService.generateToken(savedUser);

        return AuthResponse.of(
            token,
            savedUser.getId(),
            savedUser.getUsername(),
            savedUser.getEmail(),
            savedUser.getRoles().stream().map(Role::name).collect(Collectors.toSet()),
            jwtService.getExpirationTime()
        );
    }

    /**
     * Authenticate user and return JWT token.
     */
    public AuthResponse login(LoginRequest request) {
        log.info("User login attempt: {}", request.getUsername());

        // Authenticate user
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        // Get user from database
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new UserNotFoundException("User not found: " + request.getUsername()));

        // Generate JWT token
        String token = jwtService.generateToken(user);
        log.info("User logged in successfully: {}", user.getUsername());

        return AuthResponse.of(
            token,
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRoles().stream().map(Role::name).collect(Collectors.toSet()),
            jwtService.getExpirationTime()
        );
    }

    /**
     * Get user profile by username.
     */
    public UserResponse getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        return mapToUserResponse(user);
    }

    /**
     * Get user by ID.
     */
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        return mapToUserResponse(user);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .roles(user.getRoles().stream().map(Role::name).collect(Collectors.toSet()))
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }

}
