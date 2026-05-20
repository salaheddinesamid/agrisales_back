package com.example.medjool.modules.shipment.service;

import com.example.medjool.modules.order.model.Order;
import com.example.medjool.modules.shipment.model.Shipment;

import java.util.Optional;

public interface ShipmentAdderService {
    /**     * Creates a shipment based on the provided order.
     *
     * @param order the order for which the shipment is to be created
     * @throws Exception if an error occurs during shipment creation
     */
    Shipment addShipment(Order order) throws Exception;
}
