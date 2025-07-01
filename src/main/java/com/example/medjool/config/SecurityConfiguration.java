package com.example.medjool.config;

import com.example.medjool.dto.AccessDeniedErrorDto;
import com.example.medjool.filters.ApiKeyAuthenticationFilter;
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

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;

/** * Security configuration class for setting up Spring Security.
 * This class configures the security filter chain, access rules, and JWT filter.
 */


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JWTFilter JWTFilter;
    private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;

    /**     * Configures the security filter chain for the application.
     * This method sets up CORS, CSRF protection, and access rules for different endpoints.
     *
     * @param http the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .cors()
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
                                        "/api/order/**",
                                        "/api/margin_per_client/**",
                                        "/api/configuration/client/delete/**",
                                        "/api/email/**",                                        "/api-docs/**"
                                ).permitAll()
                                .requestMatchers("/api/user/**")
                                .hasAnyAuthority("GENERAL_MANAGER","IT_ADMIN")
                                .requestMatchers(

                                        "/api/stock/get_all",

                                        "/api/configuration/**",
                                        "/api/settings/**",
                                        "/api/alert/**",
                                        "/api/production/**",
                                        "/api/notification/**",
                                        "/api/shipment/**"
                                ).hasAnyAuthority("GENERAL_MANAGER","SALES","API_SERVICE")
                                .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                                .accessDeniedHandler(accessDeniedHandler())
                )
                // Add API key filter first - if API key is valid, it will authenticate and skip JWT filter
                .addFilterBefore(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // Add JWT filter second - will only process if API key filter didn't authenticate
                .addFilterBefore(JWTFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**     * Provides a custom AccessDeniedHandler that returns a JSON response when access is denied.
     * This handler sets the response status to 403 Forbidden and includes an error message in the response body.
     *
     * @return the configured AccessDeniedHandler
     */
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


    /**     * Provides a PasswordEncoder bean for encoding passwords.
     * This method uses BCryptPasswordEncoder for secure password hashing.
     *
     * @return the configured PasswordEncoder
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**     * Provides a RestTemplate bean for making HTTP requests.
     * This method creates a new instance of RestTemplate.
     *
     * @return the configured RestTemplate
     */
    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
