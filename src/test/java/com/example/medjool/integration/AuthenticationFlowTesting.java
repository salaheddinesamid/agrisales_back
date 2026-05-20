package com.example.medjool.integration;

import com.example.medjool.MedjoolApplication;
import com.example.medjool.modules.auth.dto.AuthenticationResponseDto;


import com.example.medjool.modules.auth.dto.LoginRequestDto;
import com.example.medjool.modules.auth.service.implementation.AuthenticationServiceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = MedjoolApplication.class)
@AutoConfigureMockMvc
public class AuthenticationFlowTesting {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private final AuthenticationServiceImpl authenticationService;


    @Autowired
    public AuthenticationFlowTesting(AuthenticationServiceImpl authenticationService) {
        this.authenticationService = authenticationService;
    }


    @Test
    void testLogin_ReturnsToken() throws Exception {
        // Arrange
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("salaheddine.samid@medjoolstar.com");
        loginRequest.setPassword("Salah");

        // Act
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Optionally extract the token
        String response = result.getResponse().getContentAsString();
        AuthenticationResponseDto authResponse = objectMapper.readValue(response, AuthenticationResponseDto.class);
        String token = authResponse.getToken();

        assertNotNull(token);
        System.out.println("JWT Token: " + token);
    }

    @Test
    void testLogin_withInvalidCredentials() throws Exception {
        // Implement the test logic here

        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail("salaheddine.samid@medjoolstar.com");
        loginRequestDto.setPassword("1234");

        // Act
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnauthorized()).andReturn();

    }

    @Test
    void accessUnauthorizedEndpoint() throws Exception {

        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail("user@gmail.com");
        loginRequestDto.setPassword("user");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andReturn();
        String token = result.getResponse().getContentAsString();
        System.out.println("JWT Token: " + token);
        // Act
        mockMvc.perform(get("/api/stock/get_all")
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void validJwt_grantAccess() throws Exception {
        // Arrange
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("salaheddine.samid@medjoolstar.com");
        loginRequest.setPassword("Salah");

        // Authenticate
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Extract the JWT token
        String response = result.getResponse().getContentAsString();
        AuthenticationResponseDto authResponse = objectMapper.readValue(response, AuthenticationResponseDto.class);
        String token = authResponse.getToken();


        // Access to the endpoint
        MvcResult accessResponse = mockMvc.perform(get("/api/stock/get_all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void invalidJwt_denyAccess() throws Exception {
        // Implement the test logic here
    }


}
