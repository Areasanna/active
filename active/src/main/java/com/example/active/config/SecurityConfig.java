package com.example.active.config;

import com.example.active.auth.AuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    private final AuthFilter authFilter;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/h2-console/**").permitAll()

                        // Auth e Cadastro
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()

                        // Consultas públicas
                        .requestMatchers(HttpMethod.GET, "/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/exercises/**").permitAll()

                        // Apenas Admin
                        .requestMatchers(HttpMethod.POST, "/exercises/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/exercises/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/exercises/**").hasRole("ADMIN")

                        // Consultas e Edições com login
                        .requestMatchers("/workout-sessions/**").authenticated()
                        .requestMatchers("/training-plans/**").authenticated()
                        .requestMatchers("/personal-record/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/exercises", "/training-plans/**", "/workout-sessions/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/exercises").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/exercises").authenticated()
                        .requestMatchers(HttpMethod.GET, "/exercises").authenticated()
                        .requestMatchers(HttpMethod.GET, "/workout-sessions/**", "/training-plans/**", "/exercises/{id}/personal-record/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
