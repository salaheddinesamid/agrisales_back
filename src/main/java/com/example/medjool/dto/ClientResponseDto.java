package com.example.medjool.dto;

import com.example.medjool.model.Address;
import com.example.medjool.model.Client;
import com.example.medjool.model.Contact;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ClientResponseDto {

    Integer clientId;
    String companyName;
    String generalManager;
    String companyActivity;
    private String SIRET;
    private String webSite;
    private String preferredProductQuality;
    private Float commission;
    private List<Address> addresses;
    private List<Contact> contacts;
    String clientStatus;

    public ClientResponseDto(Client client) {
        this.clientId = client.getClientId();
        this.companyName = client.getCompanyName();
        this.generalManager = client.getGeneralManager();
        this.companyActivity = client.getCompanyActivity();
        this.SIRET = client.getSIRET();
        this.webSite = client.getWebSite();
        this.preferredProductQuality = client.getPreferredProductQuality();
        this.commission = client.getCommission();
        this.addresses = client.getAddresses();
        this.contacts = client.getContacts();
        this.clientStatus = client.getClientStatus().toString();
    }
}
