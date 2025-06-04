package com.example.medjool.controller;

import com.example.medjool.dto.LoginRequestDto;
import com.example.medjool.dto.NewUserDto;
import com.example.medjool.services.implementation.AuthenticationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/** * Controller for handling authentication requests such as login and registration.
 */


@RestController
@RequestMapping("api/auth")
public class AuthenticationController {

    private final AuthenticationServiceImpl authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationServiceImpl authenticationService) {
        this.authenticationService = authenticationService;
    }

    /** * Handles user login requests.
     *
     * @param loginRequestDto the login request containing email and password
     * @return ResponseEntity with authentication result
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDto loginRequestDto) {
        return authenticationService.authenticate(loginRequestDto);
    }

    /** * Handles user registration requests.
     *
     * @param newUserDto the new user details for registration
     * @return ResponseEntity with registration result
     */
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody NewUserDto newUserDto) {
        return authenticationService.createCredentials(newUserDto);
    }

}
