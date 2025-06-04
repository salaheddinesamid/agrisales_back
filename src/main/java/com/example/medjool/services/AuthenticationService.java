package com.example.medjool.services;

import com.example.medjool.dto.LoginRequestDto;
import com.example.medjool.dto.NewUserDto;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {

    /**
     * Authenticate a user with the provided credentials.
     * @param loginRequestDto
     * @return ResponseEntity containing authentication result
     */
    ResponseEntity<?> authenticate(LoginRequestDto loginRequestDto);

    /**
     * Create new user credentials.
     * @param newUserDto
     * @return ResponseEntity containing the result of the creation
     */
    ResponseEntity<?> createCredentials(NewUserDto newUserDto);

    /**
     * Logout the user by invalidating the provided token.
     * @param token
     * @return ResponseEntity indicating the result of the logout operation
     */
    ResponseEntity<Object> logout(String token);
}
