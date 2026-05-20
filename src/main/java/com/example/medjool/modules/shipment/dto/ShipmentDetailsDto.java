package com.example.medjool.modules.shipment.dto;

import com.example.medjool.modules.order.dto.OrderResponseDto;
import lombok.Data;

@Data
public class ShipmentDetailsDto {
    private long shipmentId;
    private String trackingNumber;
    private String trackingUrl;
    private OrderResponseDto orderDetails;

}
