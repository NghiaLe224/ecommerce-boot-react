package com.fortune.project.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {
    private String username;
    private List<String> roles;
    private String jwtToken;

    public UserInfoResponse(String username, List<String> roles) {
        this.username = username;
        this.roles = roles;
    }
}


