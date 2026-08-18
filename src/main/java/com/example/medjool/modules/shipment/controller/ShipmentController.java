package com.example.medjool.modules.shipment.controller;

import com.example.medjool.modules.shipment.dto.ShipmentDetailsDto;
import com.example.medjool.modules.shipment.service.implementation.ShipmentAdderServiceImpl;
import com.example.medjool.modules.shipment.service.implementation.ShipmentQueryServiceImpl;
import com.example.medjool.modules.shipment.service.implementation.ShipmentUpdateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** * Controller for managing shipments, including retrieving all shipments,
 * updating tracking numbers, and canceling shipments.
 */


@RestController
@RequestMapping("/api/shipment")

public class ShipmentController {

    private final ShipmentAdderServiceImpl shipmentAdderService;
    private final ShipmentQueryServiceImpl shipmentQueryService;
    private final ShipmentUpdateServiceImpl shipmentUpdateService;

    @Autowired
    public ShipmentController(ShipmentAdderServiceImpl shipmentAdderService,ShipmentQueryServiceImpl shipmentQueryService, ShipmentUpdateServiceImpl shipmentUpdateService) {
        this.shipmentAdderService = shipmentAdderService;
        this.shipmentQueryService = shipmentQueryService;
        this.shipmentUpdateService = shipmentUpdateService;
    }

    /**     * Retrieves all shipments.
     *
     * @return a list of ShipmentDetailsDto containing shipment details.
     * @throws Exception if an error occurs while fetching shipments.
     */
    @GetMapping("/get_all")
    public ResponseEntity<?> getAllShipments() throws Exception {
        try{
            return ResponseEntity.status(200)
                    .body(shipmentQueryService.getAllShipments());
        }catch (Exception exception){
            return ResponseEntity.status(500)
                    .build();
        }
    }


    /**     * Updates the tracking number for a specific shipment.
     *
     * @param shipmentId the ID of the shipment to update
     * @param trackingNumber the new tracking number to set
     * @throws Exception if an error occurs while updating the tracking number
     */
    @PutMapping("/update/tracking/{shipmentId}")
    public ResponseEntity<?> updateTrackingNumber(@PathVariable long shipmentId, @RequestParam String trackingNumber) throws Exception {
        try{
            return ResponseEntity.status(200)
                    .body(
                            shipmentUpdateService.updateShipmentTracker(shipmentId, trackingNumber)
                    );
        }catch (Exception exception){
            return ResponseEntity.status(500)
                    .build();
        }
    }

    /**     * Cancels a shipment by its ID.
     *
     * @param shipmentId the ID of the shipment to cancel
     * @return ResponseEntity indicating the result of the cancellation operation
     * @throws Exception if an error occurs while canceling the shipment
     */
    @DeleteMapping("/delete/{shipmentId}")
    public ResponseEntity<String> cancelShipment(@PathVariable long shipmentId) throws Exception {
        try{
            shipmentUpdateService.cancelShipment(shipmentId);
            return ResponseEntity.ok().build();
        }catch (Exception exception){
            return ResponseEntity.status(500)
                    .build();
        }
    }

}
