package com.anascoding.auth_system.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthFilter jwtAuthFilter;
        private final CustomUserDetailsService customUserDetailsService;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws  Exception{

        http
                .csrf(AbstractHttpConfigurer::disable) // Only disable for testing
                .cors(AbstractHttpConfigurer::disable) // Only disable for testing
                .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/auth/**").permitAll()
                                .anyRequest().authenticated())
                .oauth2Client(Customizer.withDefaults())
                .userDetailsService(customUserDetailsService)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                ;


        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder(
                BCryptPasswordEncoder.BCryptVersion.$2B, 10);
    }

//    @Bean
//    public UserDetailsService userDetailsService()
//    {
//        return new CustomUserDetailsService();
//    }

    @Bean
    public AuthenticationManager authenticationManager()
    {
        return new ProviderManager(authenticationProvider());
    }

    @Bean
    public AuthenticationProvider  authenticationProvider(
            )
    {
        // our custom
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

//    Email + Password
//      ↓
//    AuthenticationManager
//      ↓
//    UserDetailsService (find by email)
//      ↓
//    BCrypt check
//      ↓
//    Authenticated



}
