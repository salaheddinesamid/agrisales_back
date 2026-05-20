package com.example.medjool.modules.auth.dto;

import lombok.Data;

@Data
public class LoginRequestDto {

    private String email;
    private String password;
}
