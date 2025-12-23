package com.idap.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Response DTO for authentication (login/register).
 * Contains JWT token and user information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String type;
    private Long userId;
    private String username;
    private String email;
    private Set<String> roles;
    private long expiresIn;

    public static AuthResponse of(String token, Long userId, String username, String email, Set<String> roles, long expiresIn) {
        return AuthResponse.builder()
            .token(token)
            .type("Bearer")
            .userId(userId)
            .username(username)
            .email(email)
            .roles(roles)
            .expiresIn(expiresIn)
            .build();
    }

}
