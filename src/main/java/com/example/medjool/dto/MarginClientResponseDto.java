package com.example.medjool.dto;

import lombok.Data;

@Data
public class MarginClientResponseDto {

    private ClientResponseDto clientResponseDto;
    private double totalWeight;
    private double totalPrice;
    private TotalPalletCostDto totalPalletCostDto;
    private Double marginOnVariableCost;
    private Double margin;
}
