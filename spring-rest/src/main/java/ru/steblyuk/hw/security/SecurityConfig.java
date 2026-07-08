package ru.steblyuk.hw.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private static final String ADMIN_ROLE_NAME = "ADMIN";
    private static final String[] GET_ACCEPTED_REQUEST_PATTERNS = {"/webjars/jquery/3.7.1/jquery.min.js", "/js/**", "/css/**",
            "/error", "/auth", "/books/**", "/authors", "/genres", "/api/*/books/**", "/api/*/authors", "/api/*/genres"};

    @Bean
    public SecurityFilterChain securityFilterChain(JwtAuthenticationFilter authenticationFilter, HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.POST, "api/*/auth/sign-in").permitAll()
                        .requestMatchers(HttpMethod.GET, GET_ACCEPTED_REQUEST_PATTERNS).permitAll()
                        .requestMatchers(HttpMethod.POST).hasRole(ADMIN_ROLE_NAME)
                        .requestMatchers(HttpMethod.PUT).hasRole(ADMIN_ROLE_NAME)
                        .requestMatchers(HttpMethod.DELETE).hasRole(ADMIN_ROLE_NAME)
                        .anyRequest().authenticated())
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
