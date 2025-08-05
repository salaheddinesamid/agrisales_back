package com.example.medjool.config;

import com.example.medjool.filters.SimpleFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class to set up CORS (Cross-Origin Resource Sharing) for the application.
 * This allows the frontend application running on a different origin to access the backend APIs.
 */


@Configuration
public class CorsConfiguration {

    /** * Configures CORS settings for the application.
     *
     * @return a WebMvcConfigurer that applies CORS settings to all endpoints.
     */

    Logger logger = LoggerFactory.getLogger(SimpleFilter.class);

    /** * Interceptor to log request details.
     */

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Apply to all endpoints
                        .allowedOrigins(
                                "http://localhost:3000",
                                "http://127.0.0.1:6000",
                                "http://192.168.15.52:3000"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow specific HTTP methods
                        .allowedHeaders("*") // Allow all headers (including Authorization)
                        .allowCredentials(true) // Required for cookies/auth headers
                        .maxAge(3600); // Cache preflight response for 1 hour
            }
        };
    }
}