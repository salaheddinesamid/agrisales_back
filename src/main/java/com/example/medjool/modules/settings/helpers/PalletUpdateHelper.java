package com.example.medjool.modules.settings.helpers;

import com.example.medjool.modules.settings.dto.UpdatePalletDto;
import com.example.medjool.modules.stock.model.Pallet;
import org.springframework.stereotype.Component;

@Component
public class PalletUpdateHelper {

    /**     * Updates the dimensions of an existing pallet.
     *
     * @param pallet the Pallet object to update
     * @param palletDto the DTO containing updated pallet dimensions
     */
    public void updatePalletDimensions(Pallet pallet, UpdatePalletDto palletDto) {
        pallet.setHeight(palletDto.getHeight());
        pallet.setWidth(palletDto.getWidth());
        pallet.setLength(palletDto.getLength());
    }

    /**     * Updates the costs of an existing pallet.
     *
     * @param pallet the Pallet object to update
     * @param palletDto the DTO containing updated pallet costs
     */
    public void updatePalletCosts(Pallet pallet, UpdatePalletDto palletDto){
        pallet.setProductionCost(palletDto.getProductionCost());
        pallet.setLaborCost(palletDto.getLaborCost());
        pallet.setPackagingCost(palletDto.getPackagingCost());
        pallet.setTransportationCost(palletDto.getTransportCost());
        pallet.setMarkUpCost(palletDto.getMarkupCost());
        pallet.setVat(palletDto.getVat());
        pallet.setPreliminaryLogisticsCost(palletDto.getPreliminaryLogistics());
        pallet.setInsuranceCost(palletDto.getInsuranceCost());
        pallet.setFuelCost(palletDto.getFuelCost());
        pallet.setDatePurchase(palletDto.getDatePurchase());
        pallet.setLaborTransportCost(palletDto.getLaborTransportCost());
        pallet.setPackagingAT(palletDto.getPackagingAT());
    }
    /**     * Updates the basic information of an existing pallet.
     *
     * @param pallet the Pallet object to update
     * @param palletDto the DTO containing updated pallet basic information
     */
    public void updatePalletBasicInformation(Pallet pallet, UpdatePalletDto palletDto) {
        pallet.setNumberOfBoxesInCarton(palletDto.getNumberOfBoxesInCarton());
        pallet.setNumberOfCartonsInStory(palletDto.getNumberOfCartonsInStory());
        pallet.setNumberOfStoriesInPallet(palletDto.getNumberOfStoriesInPallet());
        pallet.setNumberOfBoxesInStory(palletDto.getNumberOfBoxesInStory());
        pallet.setNumberOfBoxesInPallet(palletDto.getNumberOfBoxesInPallet());
        pallet.setPreparationTime(palletDto.getPreparationTime());
        pallet.setTag(palletDto.getTag());
        pallet.setTotalNet(palletDto.getTotalNet());
        pallet.setPackaging(palletDto.getPackaging());
    }
}
