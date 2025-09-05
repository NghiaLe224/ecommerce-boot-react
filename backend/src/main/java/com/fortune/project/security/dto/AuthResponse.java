package com.fortune.project.security.dto;

import com.fortune.project.dto.response.auth.UserResponse;

public record AuthResponse(String accessToken, long expiresInSecond, UserResponse userResponse) {
}
