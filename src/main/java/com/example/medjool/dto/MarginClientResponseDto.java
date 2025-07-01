package com.example.medjool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MarginClientResponseDto {

    private String clientName;
    private double totalWeight;
    private double totalPrice;
    private double ordersCost;
    private Double marginOnVariableCost;
    private Double margin;
}
