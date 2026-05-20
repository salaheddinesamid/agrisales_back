package com.example.medjool.modules.shipment.service.implementation;

import com.example.medjool.modules.order.dto.OrderResponseDto;
import com.example.medjool.modules.order.model.Order;
import com.example.medjool.modules.shipment.dto.ShipmentDetailsDto;
import com.example.medjool.modules.shipment.model.Shipment;
import com.example.medjool.modules.shipment.repository.ShipmentRepository;
import com.example.medjool.modules.shipment.service.ShipmentQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipmentQueryServiceImpl implements ShipmentQueryService {

    private final ShipmentRepository shipmentRepository;

    @Autowired
    public ShipmentQueryServiceImpl(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    @Override
    public List<ShipmentDetailsDto> getAllShipments() throws Exception {
        List<Shipment> shipments = shipmentRepository.findAll();
        return shipments.stream().map(shipment -> {
            ShipmentDetailsDto shipmentDetailsDto = new ShipmentDetailsDto();
            Order order = shipment.getOrder();
            OrderResponseDto orderResponseDto = new OrderResponseDto(order);
            shipmentDetailsDto.setShipmentId(shipment.getShipmentId());
            shipmentDetailsDto.setTrackingNumber(shipment.getTrackingNumber());
            shipmentDetailsDto.setTrackingUrl(shipment.getTrackingUrl());
            shipmentDetailsDto.setOrderDetails(orderResponseDto);
            return shipmentDetailsDto;
        }).toList();
    }
}
