package com.example.medjool.modules.client.service;

import com.example.medjool.modules.client.dto.UpdateClientDto;
import com.example.medjool.modules.client.model.Client;

public interface ClientUpdateService {
    /**     * Updates an existing client in the system.
     *
     * @param clientId the ID of the client to update
     * @param updateClientDto the data transfer object containing updated client details
     * @return a ResponseEntity containing the result of the operation
     */
    Client updateClient(Integer clientId, UpdateClientDto updateClientDto);

    /**     * Deletes a client from the system.
     *
     * @param id the ID of the client to delete
     * @return a ResponseEntity containing the result of the operation
     * @throws ClassNotFoundException if the client class is not found
     */
    void removeClient(Integer id);
}
