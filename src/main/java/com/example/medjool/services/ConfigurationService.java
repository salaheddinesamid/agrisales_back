package com.example.medjool.services;

import com.example.medjool.dto.*;
import com.example.medjool.model.Client;
import com.example.medjool.model.Pallet;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ConfigurationService {

    /**     * Adds a new client to the system.
     *
     * @param clientDto the data transfer object containing client details
     * @return a ResponseEntity containing the result of the operation
     */
    ResponseEntity<Object> addClient(ClientDto clientDto);

    /**     * Retrieves all clients from the system.
     *
     * @return a ResponseEntity containing a list of all clients
     */

    ResponseEntity<List<ClientResponseDto>> getAll();

    /**     * Updates an existing client in the system.
     *
     * @param clientId the ID of the client to update
     * @param updateClientDto the data transfer object containing updated client details
     * @return a ResponseEntity containing the result of the operation
     */
    ResponseEntity<Object> updateClient(Integer clientId, UpdateClientDto updateClientDto);

    /**     * Deletes a client from the system.
     *
     * @param id the ID of the client to delete
     * @return a ResponseEntity containing the result of the operation
     * @throws ClassNotFoundException if the client class is not found
     */
    ResponseEntity<Object> deleteClient(Integer id) throws ClassNotFoundException;

    /**     * Retrieves addresses associated with a client by their ID.
     *
     * @param id the ID of the client
     * @return a ResponseEntity containing a list of address response DTOs
     */
    ResponseEntity<List<AddressResponseDto>> getClientAddresses(Integer id);

    /**     * Retrieves addresses associated with a client by their name.
     *
     * @param clientName the name of the client
     * @return a ResponseEntity containing a list of address response DTOs
     */
    ResponseEntity<List<AddressResponseDto>> getClientAddressesByClientName(String clientName);

    /**     * Adds a new pallet to the system.
     *
     * @param palletDto the data transfer object containing pallet details
     * @return a ResponseEntity containing the result of the operation
     */
    ResponseEntity<Object> addPallet(PalletDto palletDto);

    /**     * Retrieves all pallets from the system.
     *
     * @return a ResponseEntity containing a list of all pallets
     */
    ResponseEntity<List<Pallet>> getAllPallets();


    /**     * Updates an existing pallet in the system.
     *
     * @param id the ID of the pallet to update
     * @param palletDto the data transfer object containing updated pallet details
     * @return a ResponseEntity containing the result of the operation
     */
    ResponseEntity<Object> updatePallet(Integer id, UpdatePalletDto palletDto);

    /**     * Deletes a pallet from the system.
     *
     * @param id the ID of the pallet to delete
     * @return a ResponseEntity containing the result of the operation
     */
    ResponseEntity<Object> deletePallet(Integer id);

    /**     * Retrieves all pallets by their packaging type.
     *
     * @param packaging the packaging type to filter pallets
     * @return a ResponseEntity containing a list of pallets matching the packaging type
     */
    ResponseEntity<List<Pallet>> getAllPalletsByPackaging(float packaging);

    /**     * Retrieves a pallet by its ID.
     *
     * @param id the ID of the pallet to retrieve
     * @return the Pallet object with the specified ID
     */
    Pallet getPalletById(Integer id);

}
