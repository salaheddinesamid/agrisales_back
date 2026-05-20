package com.example.medjool.modules.client.service;

import com.example.medjool.modules.client.dto.AddressResponseDto;
import com.example.medjool.modules.client.dto.ClientResponseDto;

import java.util.List;

public interface ClientQueryService {

    List<ClientResponseDto> getAllClients();
    /**     * Retrieves addresses of a client by client ID.
     *
     * @param id the ID of the client
     * @return ResponseEntity containing a list of addresses for the client
     */
    List<AddressResponseDto> getClientAddressesById(Integer id);
    /**     * Retrieves addresses of a client by client name.
     *
     * @param name   the name of the client
     * @return ResponseEntity containing a list of addresses for the client
     */
    List<AddressResponseDto> getClientAddressesByName(String name);
}
