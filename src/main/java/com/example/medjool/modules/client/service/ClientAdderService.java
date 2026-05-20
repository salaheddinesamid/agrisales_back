package com.example.medjool.modules.client.service;

import com.example.medjool.modules.client.dto.ClientDto;
import com.example.medjool.modules.client.model.Client;

public interface ClientAdderService {
    /**     * Adds a new client to the system.
     *
     * @param dto the data transfer object containing client details
     * @return a ResponseEntity containing the result of the operation
     */
    Client addClient(ClientDto dto);
}
