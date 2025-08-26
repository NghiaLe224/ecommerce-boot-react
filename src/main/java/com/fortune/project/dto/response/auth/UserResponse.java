package com.fortune.project.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String username;
    private List<String> roles;
    private String email;
    private Long id;

    public UserResponse(String username, List<String> roles){
        this.username = username;
        this.roles = roles;
    }
}
