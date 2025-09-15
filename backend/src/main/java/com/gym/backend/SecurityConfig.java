package com.gym.backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())           // opcional, para Postman
                .authorizeHttpRequests(requests -> requests
                        .anyRequest().permitAll());  // permite todas las requests sin login
        return http.build();
    }
}
