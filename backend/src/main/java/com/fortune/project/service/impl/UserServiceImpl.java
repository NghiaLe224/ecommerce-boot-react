package com.fortune.project.service.impl;

import com.fortune.project.dto.response.common.ApiResponse;
import com.fortune.project.security.dto.LoginRequest;
import com.fortune.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse<?> login(LoginRequest request) {
        return null;
    }
}
