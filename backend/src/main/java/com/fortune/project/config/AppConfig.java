package com.fortune.project.config;

import com.fortune.project.constant.AppConstant;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Optional;

@Configuration
public class AppConfig implements WebMvcConfigurer {
    @Bean
    public ModelMapper modelMapper(){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        return mapper;
    }


    @Bean
    public AuditorAware<String> auditorProvider() {
//        return () -> Optional.ofNullable("system_user"); // hoặc lấy từ SecurityContext
//        return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getName());
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()
                    || authentication.getPrincipal().equals("anonymousUser")) {
                // fallback nếu chưa login hoặc anonymous
                return Optional.of("SYSTEM");
            }

            return Optional.ofNullable(authentication.getName());
        };
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/images/**") // URL client sẽ gọi
                .addResourceLocations("file:uploads/images/"); // Nơi đọc file thật trên ổ đĩa
    }
}
