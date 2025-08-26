package com.fortune.project.controller;

import com.fortune.project.dto.response.auth.UserResponse;
import com.fortune.project.dto.response.common.ApiResponse;
import com.fortune.project.dto.response.common.PagingResponse;
import com.fortune.project.security.dto.AuthResponse;
import com.fortune.project.security.dto.LoginRequest;
import com.fortune.project.security.dto.SignUpRequest;
import com.fortune.project.security.dto.UserInfoResponse;
import com.fortune.project.security.service.UserDetailsImpl;
import com.fortune.project.service.AuthService;
import com.fortune.project.util.PaginationUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.fortune.project.constant.AppConstant.DEFAULT_SORT_BY_ID;
import static com.fortune.project.constant.ProductConstant.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse res) {
        return authService.authenticateUser(loginRequest, res);
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = "${app.jwt.refresh-cookie-name}", required = false) String refreshToken,
            HttpServletResponse res) {
        AuthResponse response = authService.refreshToken(refreshToken, res);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/auth/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest, HttpServletResponse res) {
        return ResponseEntity.ok(authService.createUser(signUpRequest, res));
    }

    @GetMapping("/auth/username")
    public String currentUsername(Authentication authentication) {
        if (authentication != null) {
            return authentication.getName();
        } else {
            return "Null";
        }
    }

    @GetMapping("/auth/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        UserInfoResponse response = new UserInfoResponse(
                userDetails.getUsername(),
                roles);

        return ResponseEntity.ok().body(new ApiResponse<>("Authenticated successfully", response, LocalDateTime.now()));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> signOutUser(HttpServletResponse res) {
        ApiResponse<?> response = authService.logout(res);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/admin/seller")
    public ResponseEntity<?> getAllSellers(
            @RequestParam(name = "pageNumber", defaultValue = DEFAULT_PAGE + "", required = false) Integer page,
            @RequestParam(name = "pageSize", defaultValue = DEFAULT_SIZE + "", required = false) Integer size,
            @RequestParam(name = "sortBy", defaultValue = DEFAULT_SORT_BY_ID, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = DEFAULT_SORT_DIR, required = false) String sortDir
    ){
        Pageable pageable = PaginationUtils.createPageable(page, size, sortBy, sortDir);
        PagingResponse<UserResponse> res = authService.getAllSellers(pageable);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/admin/seller")
    public ResponseEntity<?> registerSeller(@Valid @RequestBody SignUpRequest signUpRequest, HttpServletResponse res) {
        return ResponseEntity.ok(authService.createSeller(signUpRequest, res));
    }

}
