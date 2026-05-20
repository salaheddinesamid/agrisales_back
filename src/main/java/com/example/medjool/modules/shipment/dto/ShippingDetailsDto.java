package com.example.medjool.modules.shipment.dto;

import lombok.Data;

@Data
public class ShippingDetailsDto {

    String transportType;
    String incoterm;
    Long addressId;
}
