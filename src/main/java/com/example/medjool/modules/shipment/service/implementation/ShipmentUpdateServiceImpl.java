package com.example.medjool.modules.shipment.service.implementation;

import com.example.medjool.modules.shipment.model.Shipment;
import com.example.medjool.modules.shipment.repository.ShipmentRepository;
import com.example.medjool.modules.shipment.service.ShipmentUpdateService;
import org.springframework.stereotype.Service;
@Service
public class ShipmentUpdateServiceImpl implements ShipmentUpdateService {

    private final ShipmentRepository shipmentRepository;
    private static final String SHIPMENT_URL = "https://sensiwatch.com/trips/details/";

    public ShipmentUpdateServiceImpl(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    @Override
    public void cancelShipment(long shipmentId) throws Exception {
        Shipment shipment = shipmentRepository.findById(shipmentId).orElseThrow(() -> new Exception("Shipment not found"));
        shipmentRepository.delete(shipment);
    }

    @Override
    public Shipment updateShipmentTracker(long shipmentId, String trackingNumber) throws Exception {
        try{
            Shipment shipment = shipmentRepository.findById(shipmentId).orElseThrow(() -> new Exception("Shipment not found"));
            String trackingUrl = SHIPMENT_URL + trackingNumber;
            shipment.setTrackingNumber(trackingNumber);
            shipment.setTrackingUrl(trackingUrl);
            return shipmentRepository.save(shipment);

        }catch (Exception exception){
            throw new RuntimeException("Error updating shipment: " + exception.getMessage());
        }
    }
}
