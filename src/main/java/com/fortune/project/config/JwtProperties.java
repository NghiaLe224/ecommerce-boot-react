package com.fortune.project.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtProperties {
    private String secret;
    private long accessTokenTtl;
    private long refreshTokenTtl;
    private String issuer;

    private String refreshCookieName;
    private String refreshCookieDomain;
    private boolean refreshCookieSecure;
    private String refreshCookieSameSite;
    private String refreshCookiePath;

}
