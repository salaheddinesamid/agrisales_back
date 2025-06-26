package com.example.medjool.controller;

import com.example.medjool.dto.ShipmentDetailsDto;
import com.example.medjool.services.implementation.ShipmentServiceImpl;
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

    private final ShipmentServiceImpl shipmentService;

    @Autowired
    public ShipmentController(ShipmentServiceImpl shipmentService) {
        this.shipmentService = shipmentService;
    }

    /**     * Retrieves all shipments.
     *
     * @return a list of ShipmentDetailsDto containing shipment details.
     * @throws Exception if an error occurs while fetching shipments.
     */
    @GetMapping("/get_all")
    public List<ShipmentDetailsDto> getAllShipments() throws Exception {
        return shipmentService.getAllShipments();
    }


    /**     * Updates the tracking number for a specific shipment.
     *
     * @param shipmentId the ID of the shipment to update
     * @param trackingNumber the new tracking number to set
     * @throws Exception if an error occurs while updating the tracking number
     */
    @PutMapping("/update/tracking/{shipmentId}")
    public ResponseEntity<?> updateTrackingNumber(@PathVariable long shipmentId, @RequestParam String trackingNumber) throws Exception {
        return shipmentService.updateShipmentTracker(shipmentId, trackingNumber);
    }

    /**     * Cancels a shipment by its ID.
     *
     * @param shipmentId the ID of the shipment to cancel
     * @return ResponseEntity indicating the result of the cancellation operation
     * @throws Exception if an error occurs while canceling the shipment
     */
    @DeleteMapping("/delete/{shipmentId}")
    public ResponseEntity<String> cancelShipment(@PathVariable long shipmentId) throws Exception {
        return shipmentService.cancelShipment(shipmentId);
    }

}
