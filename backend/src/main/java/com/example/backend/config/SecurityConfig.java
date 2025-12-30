package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCryptPasswordEncoder automatic salt addition
        return new BCryptPasswordEncoder();
    }

    // zero trust security policy - disable endpoint access
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // for API: disable CSRF
        .csrf(csrf -> csrf.disable())

        // for testing enable all access
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/**").permitAll() // FOR ALL FOR DEVELOPMENT ONLY
            .anyRequest().authenticated()
        );

    return http.build();
}
}
