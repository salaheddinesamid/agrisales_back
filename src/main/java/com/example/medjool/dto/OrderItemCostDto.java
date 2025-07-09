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
        this.productionCost = pallet.getProductionCost() * numberOfPallets * pallet.getTotalNet();
        this.datePurchase = pallet.getDatePurchase() * numberOfPallets * pallet.getTotalNet();
        this.laborCost = pallet.getLaborCost() * numberOfPallets * pallet.getTotalNet();
        this.packagingCost = pallet.getPackagingCost() * numberOfPallets * pallet.getTotalNet();
        this.fuelCost = pallet.getFuelCost() * numberOfPallets * pallet.getTotalNet();
        this.transportCost = pallet.getTransportationCost() * numberOfPallets * pallet.getTotalNet();
        this.laborTransportCost = pallet.getLaborTransportCost() * numberOfPallets * pallet.getTotalNet();
        this.markupCost = pallet.getMarkUpCost() * numberOfPallets * pallet.getTotalNet();
        this.vat = pallet.getVat() * numberOfPallets * pallet.getTotalNet();
        this.preliminaryLogistics = pallet.getPreliminaryLogisticsCost() * numberOfPallets * pallet.getTotalNet();
        this.insuranceCost = pallet.getInsuranceCost() * numberOfPallets * pallet.getTotalNet();
        this.total = productionCost + datePurchase + laborCost + packagingCost +
                      fuelCost + transportCost + laborTransportCost + markupCost +
                      vat + preliminaryLogistics + insuranceCost;
    }
}
