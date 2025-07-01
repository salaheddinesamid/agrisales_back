package com.example.medjool.dto;

import com.example.medjool.model.Pallet;
import lombok.Data;

@Data
public class OrderItemCostDto {

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

    private double total;

    public OrderItemCostDto(Pallet pallet, int numberOfPallets) {
        this.productionCost = pallet.getProductionCost() * numberOfPallets;
        this.datePurchase = pallet.getDatePurchase() * numberOfPallets;
        this.laborCost = pallet.getLaborCost() * numberOfPallets;
        this.packagingCost = pallet.getPackagingCost() * numberOfPallets;
        this.fuelCost = pallet.getFuelCost() * numberOfPallets;
        this.transportCost = pallet.getTransportationCost() * numberOfPallets;
        this.laborTransportCost = pallet.getLaborTransportCost() * numberOfPallets;
        this.markupCost = pallet.getMarkUpCost() * numberOfPallets;
        this.vat = pallet.getVat() * numberOfPallets;
        this.preliminaryLogistics = pallet.getPreliminaryLogisticsCost() * numberOfPallets;
        this.insuranceCost = pallet.getInsuranceCost() * numberOfPallets;
        this.total = productionCost + datePurchase + laborCost + packagingCost +
                      fuelCost + transportCost + laborTransportCost + markupCost +
                      vat + preliminaryLogistics + insuranceCost;
    }
}
