package com.example.medjool.controller;

import com.example.medjool.dto.*;
import com.example.medjool.model.Client;
import com.example.medjool.model.Forex;
import com.example.medjool.model.Pallet;
import com.example.medjool.services.implementation.ConfigurationServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** * Controller for managing configuration settings such as clients and pallets.
 * Provides endpoints for adding, updating, deleting, and retrieving client and pallet configurations.
 */
@RestController
@RequestMapping("/api/configuration")
public class ConfigurationController {

    private final ConfigurationServiceImpl configurationService;

    public ConfigurationController(ConfigurationServiceImpl configurationService) {
        this.configurationService = configurationService;
    }

    // ----- Client Configuration: ------------------//

    /**     * Adds a new client configuration.
     *
     * @param client the client details to be added
     * @return ResponseEntity with the result of the operation
     */
    @PostMapping("/client/new")
    public ResponseEntity<Object> addNewClient(@RequestBody ClientDto client) {
        return configurationService.addClient(client);
    }

    /**     * Retrieves all client configurations.
     *
     * @return ResponseEntity containing a list of all clients
     */
    @GetMapping("client/get_all")
    public ResponseEntity<List<ClientResponseDto>> getAllClients() {
        return configurationService.getAll();
    }

    /**     * Retrieves a client configuration by its ID.
     *
     * @param clientId the ID of the client to retrieve
     * @return ResponseEntity containing the client details
     */
    @DeleteMapping("client/delete/{clientId}")
    public ResponseEntity<Object> deleteClient(@PathVariable Integer clientId) throws ClassNotFoundException {
        return configurationService.deleteClient(clientId);
    }

    /**     * Updates an existing client configuration.
     *
     * @param clientId the ID of the client to update
     * @param updateClientDto the DTO containing updated client details
     * @return ResponseEntity with the result of the update operation
     */
    @PutMapping("client/update/{clientId}")
    public ResponseEntity<Object> updateClient(@PathVariable Integer clientId, @RequestBody UpdateClientDto updateClientDto) {
        return configurationService.updateClient(clientId, updateClientDto);
    }

    /*
    @GetMapping("client/addresses/{clientId}")
    public ResponseEntity<List<AddressResponseDto>> getClientAddresses(@PathVariable Integer clientId) {
        return configurationService.getClientAddresses(clientId);
    }

     */

    /**     * Retrieves client addresses by client name.
     *
     * @param clientName the name of the client whose addresses are to be retrieved
     * @return ResponseEntity containing a list of addresses for the specified client
     */
    @GetMapping("client/addresses/{clientName}")
    public ResponseEntity<List<AddressResponseDto>> getClientAddressesByName(@PathVariable String clientName) {
        return configurationService.getClientAddressesByClientName(clientName);
    }

    // ----- Pallet Configuration: ------------------//

    /**     * Adds a new pallet configuration.
     *
     * @param palletDto the pallet details to be added
     * @return ResponseEntity with the result of the operation
     */
    @PostMapping("pallet/new")
    public ResponseEntity<Object> newPallet(@RequestBody PalletDto palletDto) {
        return configurationService.addPallet(palletDto);
    }

    /**     * Retrieves all pallet configurations.
     *
     * @return ResponseEntity containing a list of all pallets
     */
    @GetMapping("pallet/get_all")
    public ResponseEntity<List<Pallet>> getAllPallet() {
        return configurationService.getAllPallets();
    }


    /**     * Retrieves pallets by their packaging size.
     *
     * @param packaging the packaging size to filter pallets
     * @return ResponseEntity containing a list of pallets matching the specified packaging size
     */
    @GetMapping("pallet/get_by_packaging/{packaging}")
    public ResponseEntity<List<Pallet>> getPalletByPackaging(
            @PathVariable float packaging
    ) {
        return configurationService.getAllPalletsByPackaging(packaging);
    }

    /**     * Deletes a pallet configuration by its ID.
     *
     * @param palletId the ID of the pallet to delete
     * @return ResponseEntity with the result of the deletion operation
     */
    @DeleteMapping("pallet/delete/{palletId}")
    public ResponseEntity<Object> deletePallet(@PathVariable Integer palletId) {
        return configurationService.deletePallet(palletId);
    }


    /**     * Updates an existing pallet configuration.
     *
     * @param palletId the ID of the pallet to update
     * @param palletDto the DTO containing updated pallet details
     * @return ResponseEntity with the result of the update operation
     */
    @PutMapping("pallet/update/{palletId}")
    public ResponseEntity<Object> updatePallet(@PathVariable Integer palletId, @RequestBody UpdatePalletDto palletDto) {
        return configurationService.updatePallet(palletId, palletDto);
    }


    /**     * Retrieves a pallet configuration by its ID.
     *
     * @param palletId the ID of the pallet to retrieve
     * @return Pallet object containing the details of the specified pallet
     */
    @GetMapping("pallet/get_by_id/{palletId}")
    public Pallet getById(@PathVariable Integer palletId) {
        return configurationService.getPalletById(palletId);
    }

    // -------------- Forex configuration ------------------//

    @GetMapping("/forex/get_all")
    public ResponseEntity<List<Forex>> getAllForex() {
        return configurationService.getAllForex();
    }

    @PostMapping("/forex/new")
    public ResponseEntity<Object> addNewForex(@RequestBody NewForexCurrencyDto forexDto) {
        return configurationService.addForex(forexDto);
    }

    @PutMapping("/forex/update/{forexId}")
    public ResponseEntity<Object> updateForex(@PathVariable Long forexId, @RequestBody UpdateForexDto forexDto) {
        return configurationService.updateForex(forexId, forexDto);
    }


}
