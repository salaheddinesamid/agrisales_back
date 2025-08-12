package com.example.medjool.services.implementation;

import org.springframework.stereotype.Service;

@Service
public class TestService {

    public String getTestMessage() {
        return "This is a test message from TestService.";
    }
}
