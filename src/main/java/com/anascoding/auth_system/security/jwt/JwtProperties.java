package com.anascoding.auth_system.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter // no setter for security
@Component
@NoArgsConstructor @AllArgsConstructor
public class JwtProperties {

    @Value( value = "${application.security.jwt.expiration.refresh-token}")
    private long refreshTokenExpiration;

    @Value( value = "${application.security.jwt.expiration.access-token}")
    private long accessTokenExpiration; // in milliseconds

    @Value("${JWT_SECRET_KEY128}") // from IDEs environment variable
    // TODO -- Add this env to the running server environment variables
    private String SECRET_KEY;
}
