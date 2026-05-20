package com.example.medjool.modules.shipment.service;

import com.example.medjool.modules.shipment.model.Shipment;
import org.springframework.http.ResponseEntity;

public interface ShipmentUpdateService {

    /**     * Cancels a shipment with the given shipment ID.
     *
     * @param shipmentId the ID of the shipment to be canceled
     * @throws Exception if an error occurs during cancellation
     */
    void cancelShipment(long shipmentId) throws Exception;


    /**     * Updates the shipment tracker with the given tracking number.
     *
     * @param shipmentId the ID of the shipment to be updated
     * @param trackingNumber the new tracking number for the shipment
     * @throws Exception if an error occurs during the update
     */
    Shipment updateShipmentTracker(long shipmentId, String trackingNumber) throws Exception;
}
