package com.fortune.project.service;

import com.fortune.project.dto.response.common.ApiResponse;
import com.fortune.project.security.dto.LoginRequest;

public interface UserService {
    ApiResponse<?> login(LoginRequest request);
}
