package com.example.medjool.dto;

import lombok.Data;

@Data
public class TotalPalletCostDto {
    private float productionCost;
    private float datePurchase;
    private float laborCost;
    private float packagingCost;
    private float fuelCost;
    private float transportCost;
    private float laborTransportCost;
    private float markupCost;
    private float vat;
    private float preliminaryLogistics;
    private float insuranceCost;
}
