  package com.example.medjool.dto;

import lombok.Data;


@Data
public class MixedOrderItemRequestDto {

    private String productCode;
    private double percentage;
    private double pricePerKg;
    private double weight;
}
