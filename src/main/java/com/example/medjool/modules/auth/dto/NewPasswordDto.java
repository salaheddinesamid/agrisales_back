package com.example.medjool.modules.auth.dto;

import lombok.Data;

@Data
public class NewPasswordDto {
    private String oldPassword;
    private String newPassword;
}
