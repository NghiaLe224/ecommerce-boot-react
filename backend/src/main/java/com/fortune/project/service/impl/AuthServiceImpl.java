package com.fortune.project.service.impl;

import com.fortune.project.config.JwtProperties;
import com.fortune.project.dto.response.auth.UserResponse;
import com.fortune.project.dto.response.common.ApiResponse;
import com.fortune.project.dto.response.common.PagingResponse;
import com.fortune.project.entity.AppRole;
import com.fortune.project.entity.RefreshTokenEntity;
import com.fortune.project.entity.RoleEntity;
import com.fortune.project.entity.UserEntity;
import com.fortune.project.exception.ApiException;
import com.fortune.project.exception.EmailAlreadyExistsException;
import com.fortune.project.exception.ResourceNotFoundException;
import com.fortune.project.repository.RefreshTokenRepository;
import com.fortune.project.repository.RoleRepository;
import com.fortune.project.repository.UserRepository;
import com.fortune.project.security.dto.AuthResponse;
import com.fortune.project.security.dto.LoginRequest;
import com.fortune.project.security.dto.SignUpRequest;
import com.fortune.project.security.jwt.JwtService;
import com.fortune.project.security.service.UserDetailsImpl;
import com.fortune.project.service.AuthService;
import com.fortune.project.util.AuthUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthUtil authUtil;

    @Override
    public ResponseEntity<AuthResponse> authenticateUser(LoginRequest loginRequest, HttpServletResponse res) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword())
        );

        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();

        String[] authorities = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);

        List<String> frontendRoles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                .toList();

        String access = jwtService.generateAccessToken(principal.getUsername(), authorities);
        String refresh = jwtService.generateRefreshToken(principal.getUsername(), UUID.randomUUID().toString());

        UserEntity user = userRepository.findByName(principal.getUsername()).orElseThrow(() -> new ResourceNotFoundException("User", "username", principal.getUsername()));

        RefreshTokenEntity rft = RefreshTokenEntity.builder()
                .user(user)
                .token(refresh)
                .expiryDate(Instant.now().plusSeconds(jwtProperties.getRefreshTokenTtl()))
                .revoked(false)
                .build();

        refreshTokenRepository.save(rft);

        setRefreshCookie(res, refresh, jwtProperties.getRefreshTokenTtl());
        long expiresIn = Duration.ofSeconds(jwtProperties.getAccessTokenTtl()).toSeconds();

        return ResponseEntity.ok(new AuthResponse(access, expiresIn,
                new UserResponse(loginRequest.getUsername(), frontendRoles)));
    }

    @Override
    @Transactional
    public ApiResponse<?> createUser(SignUpRequest signUpRequest, HttpServletResponse res) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new EmailAlreadyExistsException(signUpRequest.getEmail());
        }

        UserEntity user = new UserEntity(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword())
        );

        Set<String> strRoles = signUpRequest.getRoles();
        Set<RoleEntity> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            RoleEntity defaultRole = roleRepository.findByRoleName(AppRole.USER)
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            roles.add(defaultRole);
        } else {
            strRoles.forEach(roleStr -> {
                AppRole appRole = AppRole.valueOf(roleStr.toUpperCase());
                RoleEntity roleEntity = roleRepository.findByRoleName(appRole)
                        .orElseThrow(() -> new RuntimeException("Role not found"));
                roles.add(roleEntity);
            });
        }

        user.setRoles(roles);
        UserEntity savedUser = userRepository.save(user);

        String[] authorities = roles.stream()
                .map(role -> "ROLE_" + role.getRoleName().name())
                .toArray(String[]::new);

        List<String> frontendRoles = roles.stream()
                .map(role -> role.getRoleName().name())
                .toList();

        String accessToken = jwtService.generateAccessToken(savedUser.getName(), authorities);
        String refreshToken = jwtService.generateRefreshToken(savedUser.getName(), UUID.randomUUID().toString());

        RefreshTokenEntity rft = RefreshTokenEntity.builder()
                .user(savedUser)
                .token(refreshToken)
                .expiryDate(Instant.now().plusSeconds(jwtProperties.getRefreshTokenTtl()))
                .revoked(false)
                .build();

        refreshTokenRepository.save(rft);

        setRefreshCookie(res, refreshToken, jwtProperties.getRefreshTokenTtl());
        long expiresIn = Duration.ofSeconds(jwtProperties.getAccessTokenTtl()).toSeconds();

        return new ApiResponse<>("Created user success",
                new AuthResponse(accessToken, expiresIn,
                        new UserResponse(signUpRequest.getUsername(), frontendRoles)),
                LocalDateTime.now());
    }

    @Override
    public AuthResponse refreshToken(String refreshToken, HttpServletResponse res) {
        if (refreshToken == null) {
            throw new ApiException("Missing refresh token");
        }

        RefreshTokenEntity tokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ApiException("Invalid refresh token"));

        if (tokenEntity.isRevoked() || tokenEntity.getExpiryDate().isBefore(Instant.now())) {
            throw new ApiException("Refresh token expired or revoked");
        }

        String username = jwtService.parse(refreshToken).getPayload().getSubject();
        var user = userRepository.findByName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        String[] authorities = user.getRoles().stream()
                .map(role -> "ROLE_" + role.getRoleName().name())
                .toArray(String[]::new);

        String access = jwtService.generateAccessToken(username, authorities);

        String newRefresh = jwtService.generateRefreshToken(username, UUID.randomUUID().toString());

        tokenEntity.setRevoked(true);
        tokenEntity.setRevokedAt(Instant.now());
        refreshTokenRepository.save(tokenEntity);

        RefreshTokenEntity newEntity = RefreshTokenEntity.builder()
                .user(user)
                .token(newRefresh)
                .expiryDate(Instant.now().plusSeconds(jwtProperties.getRefreshTokenTtl()))
                .revoked(false)
                .build();
        refreshTokenRepository.save(newEntity);

        setRefreshCookie(res, newRefresh, jwtProperties.getRefreshTokenTtl());

        return new AuthResponse(access, (int) jwtProperties.getAccessTokenTtl(),
                new UserResponse(username, user.getRoles().stream().map(r -> r.getRoleName().name()).toList()));
    }


    @Override
    public ApiResponse<?> logout(HttpServletRequest req, HttpServletResponse res) {
        String username;
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            username = jwtService.extractUsername(token);
        } else {
            username = "";
        }

        UserEntity user = userRepository.findByName(username).orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        if (user != null) {
            refreshTokenRepository.deleteByUser(user);
        }
        refreshTokenRepository.deleteByUser(user);

        Cookie cookie = new Cookie(jwtProperties.getRefreshCookieName(), "");
        cookie.setHttpOnly(true);
        cookie.setSecure(jwtProperties.isRefreshCookieSecure());
        cookie.setPath(jwtProperties.getRefreshCookiePath());
        cookie.setMaxAge(0);
        cookie.setDomain(jwtProperties.getRefreshCookieDomain());
        res.addCookie(cookie);

        return new ApiResponse<>(null, "Logged out", LocalDateTime.now());
    }

    @Override
    public PagingResponse<UserResponse> getAllSellers(Pageable pageable) {
        Page<UserEntity> users = userRepository.findByRoles_RoleName(AppRole.SELLER, pageable);
        Page<UserResponse> sellers = users.map(user -> {
            UserResponse userResponse = new UserResponse();
            userResponse.setEmail(user.getEmail());
            userResponse.setUsername(user.getName());
            userResponse.setRoles(user.getRoles().stream().map(Object::toString).toList());
            userResponse.setId(user.getId());
            return userResponse;
        });
        return new PagingResponse<>(sellers);
    }

    @Override
    public ApiResponse<?> createSeller(SignUpRequest signUpRequest, HttpServletResponse res) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new EmailAlreadyExistsException(signUpRequest.getEmail());
        }

        UserEntity user = new UserEntity(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword())
        );

        Set<String> strRoles = signUpRequest.getRoles();
        Set<RoleEntity> roles = new HashSet<>();

        strRoles.forEach(roleStr -> {
            AppRole appRole = AppRole.valueOf(roleStr.toUpperCase());
            RoleEntity roleEntity = roleRepository.findByRoleName(appRole)
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            roles.add(roleEntity);
        });

        user.setRoles(roles);
        UserEntity savedUser = userRepository.save(user);

        List<String> frontendRoles = roles.stream()
                .map(role -> role.getRoleName().name())
                .toList();

        return new ApiResponse<>("Created seller successfully",
                new UserResponse(savedUser.getName(), frontendRoles, savedUser.getEmail(), savedUser.getId()),
                LocalDateTime.now());
    }

    private void setRefreshCookie(HttpServletResponse res, String value, long ttlSeconds) {
        Cookie cookie = new Cookie(jwtProperties.getRefreshCookieName(), value);
        cookie.setHttpOnly(true);
        cookie.setSecure(jwtProperties.isRefreshCookieSecure());
        cookie.setPath(jwtProperties.getRefreshCookiePath());
        cookie.setMaxAge((int) ttlSeconds);
        if (jwtProperties.getRefreshCookieDomain() != null && !jwtProperties.getRefreshCookieDomain().isBlank())
            cookie.setDomain(jwtProperties.getRefreshCookieDomain());

        res.addHeader("Set-Cookie", String.format(
                "%s=%s; Max-Age=%d; Path=%s; Domain=%s; HttpOnly; Secure; SameSite=%s",
                jwtProperties.getRefreshCookieName(), value, ttlSeconds,
                jwtProperties.getRefreshCookiePath(),
                jwtProperties.getRefreshCookieDomain(),
                jwtProperties.getRefreshCookieSameSite()
        ));
    }
}
