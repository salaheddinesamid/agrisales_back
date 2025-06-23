package com.example.medjool.dto;

import lombok.Data;


@Data
public class MixedOrderItemRequestDto {

    private long productId;
    private String productCode;
    private double percentage;
    private double pricePerKg;
    private double weight;
}
