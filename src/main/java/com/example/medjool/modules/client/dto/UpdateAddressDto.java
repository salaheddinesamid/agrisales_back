package com.example.medjool.modules.client.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAddressDto {
    private long addressId;
    private String country;
    private String city;
    private String state;
    private String zip;
    private String street;
}
