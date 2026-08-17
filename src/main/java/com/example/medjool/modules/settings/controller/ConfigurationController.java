package com.example.medjool.modules.settings.controller;

import com.example.medjool.modules.client.service.implementation.ClientAdderServiceImpl;
import com.example.medjool.modules.client.service.implementation.ClientQueryServiceImpl;
import com.example.medjool.modules.client.service.implementation.ClientUpdateServiceImpl;
import com.example.medjool.modules.settings.dto.NewForexCurrencyDto;
import com.example.medjool.modules.settings.dto.PalletDto;
import com.example.medjool.modules.settings.dto.UpdateForexDto;
import com.example.medjool.modules.settings.dto.UpdatePalletDto;
import com.example.medjool.modules.settings.model.Forex;
import com.example.medjool.modules.client.dto.AddressResponseDto;
import com.example.medjool.modules.client.dto.ClientDto;
import com.example.medjool.modules.client.dto.ClientResponseDto;
import com.example.medjool.modules.client.dto.UpdateClientDto;
import com.example.medjool.modules.settings.service.implementation.*;
import com.example.medjool.modules.stock.model.Pallet;
import org.apache.coyote.Response;
import org.hibernate.engine.config.internal.ConfigurationServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** * Controller for managing configuration settings such as clients and pallets.
 * Provides endpoints for adding, updating, deleting, and retrieving client and pallet configurations.
 */
@RestController
@RequestMapping("/api/configuration")
public class ConfigurationController {

    private final PalletAdderServiceImpl palletAdderService;
    private final PalletQueryServiceImpl palletQueryService;
    private final PalletUpdaterServiceImpl palletUpdaterService;
    private final SystemSettingServiceImpl systemSettingService;
    private final ClientAdderServiceImpl clientAdderService;
    private final ClientUpdateServiceImpl clientUpdateService;
    private final ClientQueryServiceImpl clientQueryService;
    private final ForexAdderServiceImpl forexAdderService;
    private final ForexUpdaterServiceImpl forexUpdaterService;

    public ConfigurationController(PalletAdderServiceImpl palletAdderService, PalletQueryServiceImpl palletQueryService, PalletUpdaterServiceImpl palletUpdaterService, SystemSettingServiceImpl systemSettingService, ClientAdderServiceImpl clientAdderService, ClientUpdateServiceImpl clientUpdateService, ClientQueryServiceImpl clientQueryService, ForexAdderServiceImpl forexAdderService, ForexUpdaterServiceImpl forexUpdaterService) {
        this.palletAdderService = palletAdderService;
        this.palletQueryService = palletQueryService;
        this.palletUpdaterService = palletUpdaterService;
        this.systemSettingService = systemSettingService;
        this.clientAdderService = clientAdderService;
        this.clientUpdateService = clientUpdateService;
        this.clientQueryService = clientQueryService;
        this.forexAdderService = forexAdderService;
        this.forexUpdaterService = forexUpdaterService;
    }

    // ----- Client Configuration: ------------------//

    /**     * Adds a new client configuration.
     *
     * @param client the client details to be added
     * @return ResponseEntity with the result of the operation
     */
    @PostMapping("/client/new")
    public ResponseEntity<Object> addNewClient(@RequestBody ClientDto client) {
        try{
            return ResponseEntity.status(200)
                    .body(clientAdderService.addClient(client));
        }catch (Exception exception){
            return ResponseEntity.status(500)
                    .body("An error occurred during client addition?");
        }
    }

    /**     * Retrieves all client configurations.
     *
     * @return ResponseEntity containing a list of all clients
     */
    @GetMapping("client/get_all")
    public ResponseEntity<List<ClientResponseDto>> getAllClients() {
        return ResponseEntity.ok(
                clientQueryService.getAllClients()
        );
    }

    /**     * Retrieves a client configuration by its ID.
     *
     * @param clientId the ID of the client to retrieve
     * @return ResponseEntity containing the client details
     */
    @DeleteMapping("client/delete/{clientId}")
    public ResponseEntity<Object> deleteClient(@PathVariable Integer clientId) throws ClassNotFoundException {
        try{
            clientUpdateService.removeClient(clientId);
            return ResponseEntity.status(200)
                    .body(String.format("The client with ID: %s has been removed", clientId.toString()));
        }catch (Exception exception){
            return ResponseEntity.status(200)
                    .body("An error occurred during client removal");
        }
    }

    /**     * Updates an existing client configuration.
     *
     * @param clientId the ID of the client to update
     * @param updateClientDto the DTO containing updated client details
     * @return ResponseEntity with the result of the update operation
     */
    @PutMapping("client/update/{clientId}")
    public ResponseEntity<Object> updateClient(@PathVariable Integer clientId, @RequestBody UpdateClientDto updateClientDto) {
        try{
            return ResponseEntity.ok(
                    clientUpdateService.updateClient(clientId, updateClientDto)
            );
        }catch (Exception exception){
            return ResponseEntity.status(500)
                    .body(exception.getMessage());
        }
    }

    /*
    @GetMapping("client/addresses/{clientId}")
    public ResponseEntity<List<AddressResponseDto>> getClientAddresses(@PathVariable Integer clientId) {
        return configurationService.getClientAddresses(clientId);
    }

     */

    /**     * Retrieves client addresses by client name.
     *
     * @param clientId the name of the client whose addresses are to be retrieved
     * @return ResponseEntity containing a list of addresses for the specified client
     */
    @GetMapping("client/addresses")
    public ResponseEntity<?> getClientAddressesByName(@RequestParam int clientId) {
        try{
            return ResponseEntity.status(200)
                    .body(clientQueryService.getClientAddressesById(clientId));
        }catch (Exception e){
            return ResponseEntity.status(500)
                    .body(e.getMessage());
        }
    }

    // ----- Pallet Configuration: ------------------//

    /**     * Adds a new pallet configuration.
     *
     * @param palletDto the pallet details to be added
     * @return ResponseEntity with the result of the operation
     */
    @PostMapping("pallet/new")
    public ResponseEntity<Object> newPallet(@RequestBody PalletDto palletDto) {
        try{
            return ResponseEntity.status(200)
                    .body(palletAdderService.addPallet(palletDto));
        }catch (Exception e){
            return ResponseEntity.status(500)
                    .body("An error occurred during pallet creation");
        }
    }

    /**     * Retrieves all pallet configurations.
     *
     * @return ResponseEntity containing a list of all pallets
     */
    @GetMapping("pallet/get_all")
    public ResponseEntity<?> getAllPallet() {
        try{
            return ResponseEntity.ok(
                    palletQueryService.getAllPallets()
            );
        }catch (Exception exception){
            return ResponseEntity.status(500)
                    .body("");
        }
    }


    /**     * Retrieves pallets by their packaging size.
     *
     * @param packaging the packaging size to filter pallets
     * @return ResponseEntity containing a list of pallets matching the specified packaging size
     */
    @GetMapping("pallet/get_by_packaging/{packaging}")
    public ResponseEntity<?> getPalletByPackaging(
            @PathVariable float packaging
    ) {

        try{
            return ResponseEntity.status(200)
                    .body(palletQueryService.getAllPalletsByPackaging(packaging));
        }catch (Exception exception){
            return ResponseEntity.status(500)
                    .body("An error occurred when fetching pallets by packaging");
        }
    }

    /**     * Deletes a pallet configuration by its ID.
     *
     * @param palletId the ID of the pallet to delete
     * @return ResponseEntity with the result of the deletion operation
     */
    @DeleteMapping("pallet/delete/{palletId}")
    public ResponseEntity<Object> deletePallet(@PathVariable Integer palletId) {
        try{
            palletUpdaterService.removePallet(palletId);
            return ResponseEntity.status(200)
                    .body(String.format("The pallet with ID: %s has been deleted successfully", palletId.toString()));
        }catch (Exception e){
            return ResponseEntity.status(500)
                    .body("An error occurred, please try again");
        }
    }


    /**     * Updates an existing pallet configuration.
     *
     * @param palletId the ID of the pallet to update
     * @param palletDto the DTO containing updated pallet details
     * @return ResponseEntity with the result of the update operation
     */
    @PutMapping("pallet/update/{palletId}")
    public ResponseEntity<Object> updatePallet(@PathVariable Integer palletId, @RequestBody UpdatePalletDto palletDto) {
        try{
            return ResponseEntity.ok(
                    palletUpdaterService.updatePallet(palletId, palletDto)
            );
        }catch (Exception exception){
            return ResponseEntity.status(500)
                    .body("An error occurred, please try again");
        }
    }


    /**     * Retrieves a pallet configuration by its ID.
     *
     * @param palletId the ID of the pallet to retrieve
     * @return Pallet object containing the details of the specified pallet
     */
    @GetMapping("pallet/get_by_id/{palletId}")
    public ResponseEntity<?> getById(@PathVariable Integer palletId) {
        try{
            return ResponseEntity.status(200)
                    .body(palletQueryService.getPalletById(palletId));
        }catch (Exception e){
            return ResponseEntity.status(500)
                    .body(e.getMessage());
        }
    }

    // -------------- Forex configuration ------------------//


    /**     * Retrieves all forex configurations.
     *
     * @return ResponseEntity containing a list of all forex configurations
     */
    @GetMapping("/forex/get_all")
    public ResponseEntity<?> getAllForex() {
        try{
            return ResponseEntity.ok(
                    ""
            );
        }catch (Exception exception){
            return ResponseEntity.status(500)
                    .body("An error occurred, please try again");
        }
    }


    /**     * Adds a new forex configuration.
     *
     * @param forexDto the forex details to be added
     * @return ResponseEntity with the result of the operation
     */
    @PostMapping("/forex/new")
    public ResponseEntity<?> addNewForex(@RequestBody NewForexCurrencyDto forexDto) {
        try{
            return ResponseEntity.status(200)
                    .body(forexAdderService.addForex(forexDto));
        }catch (Exception exception){
            return ResponseEntity.status(500).build();
        }
    }


    /**     * Update a forex configuration by its ID.
     *
     * @param forexId the ID of the forex to delete
     * @return ResponseEntity with the result of the deletion operation
     */
    @PutMapping("/forex/update/{forexId}")
    public ResponseEntity<Object> updateForex(@PathVariable Long forexId, @RequestBody UpdateForexDto forexDto) {
        try{
            return ResponseEntity.status(200)
                    .body(forexUpdaterService.updateForex(forexId, forexDto));
        }catch (Exception e){
            return ResponseEntity.status(500)
                    .body(e.getMessage());
        }
    }


}
