package com.example.medjool.modules.shipment.service;

import com.example.medjool.modules.shipment.dto.ShipmentDetailsDto;
import com.example.medjool.modules.order.model.Order;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface ShipmentService {





    /**     * Tracks a shipment with the given shipment ID.
     *
     * @param shipmentId the ID of the shipment to be tracked
     * @throws Exception if an error occurs during tracking
     */
    void trackShipment(String shipmentId) throws Exception;


}