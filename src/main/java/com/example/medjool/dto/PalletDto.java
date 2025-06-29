package com.example.medjool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PalletDto {


    // Basic information:
    float  packaging;
    Integer numberOfBoxesInCarton;
    Integer numberOfCartonsInStory;
    Integer numberOfStoriesInPallet;
    Integer numberOfBoxesInStory;
    Integer numberOfBoxesInPallet;
    // Pallet costs:
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

    // Dimension of the pallet
    float height;
    float width;
    float length;
    Float totalNet;
    // Additional Information
    float preparationTime;
    String tag;
    String notes;
}
