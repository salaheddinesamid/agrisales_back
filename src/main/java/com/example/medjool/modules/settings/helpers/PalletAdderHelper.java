package com.example.medjool.modules.settings.helpers;

import com.example.medjool.modules.settings.dto.PalletDto;
import com.example.medjool.modules.stock.model.Pallet;
import org.springframework.stereotype.Component;

@Component
public class PalletAdderHelper {

    /**     * Adds dimensions to a new pallet.
     *
     * @param pallet the Pallet object to update
     * @param palletDto the DTO containing pallet dimensions
     */
    public void addPalletDimensions(Pallet pallet, PalletDto palletDto) {
        pallet.setHeight(palletDto.getHeight());
        pallet.setWidth(palletDto.getWidth());
        pallet.setLength(palletDto.getLength());

    }

    /**     * Adds costs to a new pallet.
     *
     * @param pallet the Pallet object to update
     * @param palletDto the DTO containing pallet costs
     */
    public void addPalletCosts(Pallet pallet, PalletDto palletDto) {
        pallet.setProductionCost(palletDto.getProductionCost());
        pallet.setDatePurchase(palletDto.getDatePurchase());
        pallet.setLaborCost(palletDto.getLaborCost());
        pallet.setPackagingCost(palletDto.getPackagingCost());
        pallet.setFuelCost(palletDto.getFuelCost());
        pallet.setTransportationCost(palletDto.getTransportCost());
        pallet.setPackagingAT(palletDto.getPackagingAT());
        pallet.setLaborTransportCost(palletDto.getLaborTransportCost());
        pallet.setMarkUpCost(palletDto.getMarkupCost());
        pallet.setVat(palletDto.getVat());
        pallet.setPreliminaryLogisticsCost(palletDto.getPreliminaryLogistics());
        pallet.setInsuranceCost(palletDto.getInsuranceCost());
    }
}
