package com.fortune.project.security;

import com.fortune.project.entity.AppRole;
import com.fortune.project.entity.RoleEntity;
import com.fortune.project.entity.UserEntity;
import com.fortune.project.repository.RoleRepository;
import com.fortune.project.repository.UserRepository;
import com.fortune.project.security.jwt.AuthEntryPointJwt;
import com.fortune.project.security.jwt.FortuneAccessDeniedHandler;
import com.fortune.project.security.jwt.JWTAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final AuthEntryPointJwt unauthorizedHandler;
    private final JWTAuthenticationFilter JWTAuthenticationFilter;
    private final FortuneAccessDeniedHandler accessDeniedHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler).accessDeniedHandler(accessDeniedHandler))
                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authRequest -> authRequest
                        // VNPay return (browser redirect) – GET
                        .requestMatchers(HttpMethod.GET, "/api/payments/vnpay-return").permitAll()
                        // VNPay IPN (server-to-server)
                        .requestMatchers(HttpMethod.POST, "/api/payments/vnpay-ipn").permitAll()
                        .requestMatchers("/error", "/error/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/seller/**").hasAnyRole("ADMIN", "SELLER")
                        .requestMatchers("/api/auth/**",
                                "/v3/api-docs/**",
                                "/h2-console/**",
                                "/swagger-ui/**",
                                "/api/public/**",
                                "/api/test/**",
                                "/images/**"
                        ).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(JWTAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .build();
    }

    //Allow static resources
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return(web -> web.ignoring().requestMatchers(
                "/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "swagger-ui.html",
                "/webjars/**",
                "/uploads/**"
        ));
    }

    // CORS Source configurable từ properties
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.security.cors.allowed-origins}") String origins,
            @Value("${app.security.cors.allowed-methods}") String methods,
            @Value("${app.security.cors.allowed-headers}") String headers,
            @Value("${app.security.cors.allow-credentials}") boolean allowCreds
    ) {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(Arrays.stream(origins.split(",")).map(String::trim).toList());
        cfg.setAllowedMethods(Arrays.stream(methods.split(",")).map(String::trim).toList());
        cfg.setAllowedHeaders(Arrays.stream(headers.split(",")).map(String::trim).toList());
        cfg.setAllowCredentials(allowCreds);
        cfg.setExposedHeaders(List.of("Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

//    @Bean
//    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
//        return args -> {
//            // Retrieve or create Roles
//            RoleEntity userRole = roleRepository.findByRoleName(AppRole.USER)
//                    .orElseGet(() -> {
//                        RoleEntity newUserRole = new RoleEntity(AppRole.USER);
//                        return roleRepository.save(newUserRole);
//                    });
//
//            RoleEntity sellerRole = roleRepository.findByRoleName(AppRole.SELLER)
//                    .orElseGet(() -> {
//                        RoleEntity newSellerRole = new RoleEntity(AppRole.SELLER);
//                        return roleRepository.save(newSellerRole);
//                    });
//
//            RoleEntity adminRole = roleRepository.findByRoleName(AppRole.ADMIN)
//                    .orElseGet(() -> {
//                        RoleEntity newAdminRole = new RoleEntity(AppRole.ADMIN);
//                        return roleRepository.save(newAdminRole);
//                    });
//
//            Set<RoleEntity> userRoles = Set.of(userRole);
//            Set<RoleEntity> sellerRoles = Set.of(sellerRole);
//            Set<RoleEntity> adminRoles = Set.of(userRole, sellerRole, adminRole);
//
//
//            // Create users if not already present
//            if (!userRepository.existsByName("user1")) {
//                UserEntity user1 = new UserEntity("user1", "user1@example.com", passwordEncoder.encode("password1"));
//                userRepository.save(user1);
//            }
//
//            if (!userRepository.existsByName("seller1")) {
//                UserEntity seller1 = new UserEntity("seller1", "seller1@example.com", passwordEncoder.encode("password2"));
//                userRepository.save(seller1);
//            }
//
//            if (!userRepository.existsByName("admin")) {
//                UserEntity admin = new UserEntity("admin", "admin@example.com", passwordEncoder.encode("adminPass"));
//                userRepository.save(admin);
//            }
//
//            // Update Roles for existing users
//            userRepository.findByName("user1").ifPresent(user -> {
//                user.setRoles(userRoles);
//                userRepository.save(user);
//            });
//
//            userRepository.findByName("seller1").ifPresent(seller -> {
//                seller.setRoles(sellerRoles);
//                userRepository.save(seller);
//            });
//
//            userRepository.findByName("admin").ifPresent(admin -> {
//                admin.setRoles(adminRoles);
//                userRepository.save(admin);
//            });
//        };
//    }


}
