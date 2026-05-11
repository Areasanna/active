package com.example.active.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desabilita para APIs REST
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/h2-console/**", "/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users", "/exercises/**").permitAll()// Cadastro é aberto
                        .requestMatchers(HttpMethod.GET, "/users/**", "/exercises/**", "/muscles", "/equipment").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/users/**", "/exercises/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE,"/users/**", "/exercises/**").permitAll()
                        .anyRequest().authenticated() // Todo o resto precisa de login
                );
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authManager (AuthenticationConfiguration config) throws Exception{
         return config.getAuthenticationManager();
    }
}