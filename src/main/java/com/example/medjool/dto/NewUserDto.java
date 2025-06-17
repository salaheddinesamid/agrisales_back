package com.example.medjool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDto {

    private String firstName;
    private String lastName;
    private String email;
    private String roleName;
    private String password;
    private boolean accountLocked;
}
