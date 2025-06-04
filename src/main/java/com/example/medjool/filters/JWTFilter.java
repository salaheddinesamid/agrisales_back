package com.example.medjool.filters;

import com.example.medjool.exception.TokenExpiredException;
import com.example.medjool.exception.UserAccountLockedException;
import com.example.medjool.exception.UserNotFoundException;
import com.example.medjool.jwt.JwtUtilities;
import com.example.medjool.services.implementation.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JwtUtilities jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            System.out.println(request.getLocalAddr());

            // Extract the token from the HTTP headers
            String token = jwtUtil.getToken(request);
            System.out.println(token);
            if (token != null && jwtUtil.validateToken(token)) {
                // Extract the username
                String email = jwtUtil.extractUserName(token);
                // Load user details
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                if (userDetails != null && userDetails.isAccountNonLocked()) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails.
                                    getUsername(), null, userDetails.getAuthorities());
                    log.info("authenticated user with email :{}", email);

                    // Set the authentication context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                else if(userDetails != null && !userDetails.isAccountNonLocked()) {
                    throw new UserAccountLockedException("User account is locked");
                }
                else {
                    throw new UserNotFoundException();
                }
            }
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException ex) {
            log.error("JWT Token expired: {}", ex.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token has expired", "JWT_EXPIRED");
        } catch (TokenExpiredException ex) {
            log.error("JWT Token expired: {}", ex.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, ex.getMessage(), "JWT_EXPIRED");
        } catch (UserAccountLockedException ex) {
            log.error("User account locked: {}", ex.getMessage());
            sendErrorResponse(response, HttpStatus.FORBIDDEN, ex.getMessage(), "ACCOUNT_LOCKED");
        } catch (UserNotFoundException ex) {
            log.error("User not found: {}", ex.getMessage());
            sendErrorResponse(response, HttpStatus.NOT_FOUND, "User not found", "USER_NOT_FOUND");
        } catch (Exception ex) {
            log.error("Authentication error: {}", ex.getMessage());
            sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Authentication failed", "AUTH_ERROR");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message, String errorCode) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", status.value());
        errorDetails.put("error", status.getReasonPhrase());
        errorDetails.put("message", message);
        errorDetails.put("errorCode", errorCode);

        objectMapper.writeValue(response.getWriter(), errorDetails);
    }
}