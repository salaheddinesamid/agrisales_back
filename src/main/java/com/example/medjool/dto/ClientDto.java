package com.example.medjool.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ClientDto {
    private String companyName;
    private String generalManager;
    private String companyActivity;
    private String website;
    private String preferredProductQuality;
    private float commission;
    private List<ContactDto> contacts;
    private List<AddressDto> addresses;
    private String status;
    private String siret;
}
