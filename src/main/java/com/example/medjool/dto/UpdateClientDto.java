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
    private List<Long> deletedAddresses;
    private List<Long> deletedContacts;
    private String website;
    private String clientStatus;
    private Float commission;
    private String siret;
}
