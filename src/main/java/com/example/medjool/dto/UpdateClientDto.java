package com.example.medjool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateClientDto {

    private String newCompanyName;
    private String newGeneralManager;
    private String newCompanyActivity;
    private List<UpdateAddressDto> newAddresses;
    private List<UpdateContactDto> newContacts;
    private String website;
    private String clientStatus;
    private String siret;
}
