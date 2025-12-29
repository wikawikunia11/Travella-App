package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


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
            .requestMatchers(new AntPathRequestMatcher("/**")).permitAll() // PUBLICZNY DOSTĘP
            .anyRequest().authenticated()
        );

    return http.build();
}
}
