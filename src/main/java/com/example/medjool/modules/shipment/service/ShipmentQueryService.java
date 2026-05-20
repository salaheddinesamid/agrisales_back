package com.example.medjool.modules.shipment.service;

import com.example.medjool.modules.shipment.dto.ShipmentDetailsDto;

import java.util.List;

public interface ShipmentQueryService {

    /**     * Retrieves all shipments.
     *
     * @return a list of ShipmentDetailsDto containing details of all shipments
     * @throws Exception if an error occurs while retrieving shipments
     */
    List<ShipmentDetailsDto> getAllShipments() throws Exception;
}
