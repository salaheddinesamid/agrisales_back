package com.example.medjool.modules.shipment.service.implementation;

import com.example.medjool.modules.order.model.Order;
import com.example.medjool.modules.shipment.model.Shipment;
import com.example.medjool.modules.shipment.repository.ShipmentRepository;
import com.example.medjool.modules.shipment.service.ShipmentAdderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShipmentAdderServiceImpl implements ShipmentAdderService {

    private final ShipmentRepository shipmentRepository;
    @Autowired
    public ShipmentAdderServiceImpl(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    @Override
    public Shipment addShipment(Order order) throws Exception {
        try{
            Shipment shipment = new Shipment();
            shipment.setOrder(order);
            return shipmentRepository.save(shipment);
        }
        catch (Exception e){
            throw new RuntimeException("Error creating shipment: " + e.getMessage());
        }
    }
}
