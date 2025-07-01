package com.example.medjool.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatePalletDto {

    Long palletId;
    float  packaging;
    Integer numberOfBoxesInCarton;
    Integer numberOfCartonsInStory;
    Integer numberOfStoriesInPallet;
    @NotNull
    private Integer numberOfBoxesInStory;
    @NotNull
    private Integer numberOfBoxesInPallet;

    // Pallet costs:
    @NotNull
    private float productionCost;
    @NotNull
    private float datePurchase;
    @NotNull
    private float laborCost;
    @NotNull
    private float packagingCost;
    @NotNull
    private float fuelCost;
    @NotNull
    private float transportCost;
    @NotNull
    private float laborTransportCost;
    @NotNull
    private float markupCost;
    @NotNull
    private float vat;
    @NotNull
    private float preliminaryLogistics;
    @NotNull
    private float insuranceCost;

    float height;
    float width;
    float length;

    Float totalNet;
    float preparationTime;
    String tag;
    String notes;

}
