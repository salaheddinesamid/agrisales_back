package com.example.medjool.services;

import com.example.medjool.dto.ShipmentDetailsDto;
import com.example.medjool.model.Order;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface ShipmentService {

    /**     * Creates a shipment based on the provided order.
     *
     * @param order the order for which the shipment is to be created
     * @throws Exception if an error occurs during shipment creation
     */
    void createShipment(Optional<Order> order) throws Exception;

    /**     * Cancels a shipment with the given shipment ID.
     *
     * @param shipmentId the ID of the shipment to be canceled
     * @return a ResponseEntity indicating the result of the cancellation
     * @throws Exception if an error occurs during cancellation
     */
    ResponseEntity<String> cancelShipment(long shipmentId) throws Exception;


    /**     * Updates the shipment tracker with the given tracking number.
     *
     * @param shipmentId the ID of the shipment to be updated
     * @param trackingNumber the new tracking number for the shipment
     * @throws Exception if an error occurs during the update
     */
    void updateShipmentTracker(long shipmentId, String trackingNumber) throws Exception;

    /**     * Tracks a shipment with the given shipment ID.
     *
     * @param shipmentId the ID of the shipment to be tracked
     * @throws Exception if an error occurs during tracking
     */
    void trackShipment(String shipmentId) throws Exception;

    /**     * Retrieves all shipments.
     *
     * @return a list of ShipmentDetailsDto containing details of all shipments
     * @throws Exception if an error occurs while retrieving shipments
     */
    List<ShipmentDetailsDto> getAllShipments() throws Exception;
}