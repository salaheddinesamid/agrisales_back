package com.example.medjool.config;

import com.example.medjool.dto.AccessDeniedErrorDto;
import com.example.medjool.filters.JWTFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JWTFilter JWTFilter;

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {

                http.
                        cors()
                                .and()

                        .csrf()
                        .disable()
                        .authorizeHttpRequests(authorizeRequests ->
                                authorizeRequests.requestMatchers(
                                        "/api/auth/**",
                                        "/swagger-ui/**",
                                        "/actuator/metrics/",
                                        "/actuator/health",
                                        "/actuator/info",
                                        "/api-docs/**"

                                                ).permitAll()
                                        .requestMatchers("/api/order/**","/api/stock/get_all","/api/overview/**",
                                                "/api/configuration/**",
                                                "/api/settings/**",
                                                "/api/alert/**",
                                                "/api/production/**",
                                                "/api/user/**",
                                                "/api/notification/**",
                                                "/api/shipment/**").hasAnyAuthority("GENERAL_MANAGER","SALES")
                                        .anyRequest().authenticated()

                        ).exceptionHandling(exceptionHandling ->
                                exceptionHandling
                                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                                        .accessDeniedHandler(accessDeniedHandler())
                        )
                        //.addFilterBefore(JWTExpirationCheckFilter, UsernamePasswordAuthenticationFilter.class)
                        .addFilterBefore(JWTFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.FORBIDDEN.value());

            // Create error response DTO
            AccessDeniedErrorDto errorDto = AccessDeniedErrorDto.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.FORBIDDEN.value())
                    .error("Forbidden")
                    .message("Access denied, You don't have the authority to access this service. " )
                    .path(request.getRequestURI())
                    .build();

            // Serialize to JSON
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule()); // For LocalDateTime support
                objectMapper.writeValue(response.getOutputStream(), errorDto);
            } catch (IOException e) {
                throw new RuntimeException("Failed to write error response", e);
            }
        };
    }


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
