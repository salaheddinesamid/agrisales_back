package com.example.medjool.modules.user_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserDetailsDto {
    private long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private boolean active;
    private LocalDateTime lastLogin;
}
