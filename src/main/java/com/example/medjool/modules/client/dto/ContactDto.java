package com.example.medjool.modules.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContactDto {

    private String department;
    private String email;
    private String phone;
}
