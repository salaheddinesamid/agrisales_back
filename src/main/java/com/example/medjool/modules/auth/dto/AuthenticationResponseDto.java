package com.example.medjool.modules.auth.dto;

import com.example.medjool.modules.user_management.dto.UserDetailsDto;
import lombok.Data;

@Data
public class AuthenticationResponseDto {
    String token;
    UserDetailsDto user;
}
