package com.example.medjool.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Configuration
public class SimpleFilter extends OncePerRequestFilter {


    Logger logger = LoggerFactory.getLogger(SimpleFilter.class);

    /**     * This method is called for every request to log the request URI and method.
     * It continues the filter chain after logging.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if an error occurs during filtering
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.info("Incoming request: {}Type of request:{}", request.getRequestURI(), request.getMethod());
        logger.info("MAC Address: {}", request.getHeader("X-MAC-Address"));

        // IMPORTANT: Continue the filter chain
        filterChain.doFilter(request, response);

        // You can also log the response if needed
        logger.info("Outgoing response with status: {}", response.getStatus());
    }
}
