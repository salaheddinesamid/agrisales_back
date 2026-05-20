package com.example.medjool.modules.client.mapper;

import com.example.medjool.modules.client.dto.AddressDto;
import com.example.medjool.modules.client.dto.AddressResponseDto;
import com.example.medjool.modules.client.model.Address;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddressMapper {
    public List<Address> mapToAddress(List<AddressDto> dto){
        return dto.stream().map(addressDto -> {
            Address address = new Address();
            address.setCity(addressDto.getCity());
            address.setCountry(addressDto.getCountry());
            address.setStreet(addressDto.getStreet());
            address.setState(addressDto.getState());
            address.setPostalCode(addressDto.getPostalCode());
            return address;
        }).toList();
    }

    public List<AddressResponseDto> mapToDto(List<Address> addresses){
        return
                addresses.stream().map(AddressResponseDto::new)
                        .toList();
    }
}
