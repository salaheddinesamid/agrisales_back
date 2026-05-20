package com.example.medjool.modules.client.dto;

import com.example.medjool.modules.client.model.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponseDto {

    private Long addressId;
    private String country;
    private String city;
    private String state;
    private String postalCode;
    private String street;

    public AddressResponseDto(Address address){
        this.addressId = address.getAddressId();
        this.country = address.getCountry();
        this.city = address.getCity();
        this.state = address.getState();
        this.postalCode = address.getPostalCode();
        this.street = address.getStreet();
    }
}
