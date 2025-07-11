package com.example.medjool.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
    private float packagingAT;
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

    public UpdatePalletDto(
            float packaging,
            Integer numberOfBoxesInCarton,
            Integer numberOfCartonsInStory,
            Integer numberOfStoriesInPallet,
            @NotNull Integer numberOfBoxesInStory,
            @NotNull Integer numberOfBoxesInPallet,
            @NotNull float productionCost,
            @NotNull float datePurchase,
            @NotNull float laborCost,
            @NotNull float packagingCost,
            @NotNull float fuelCost,
            @NotNull float transportCost,
            @NotNull float laborTransportCost,
            @NotNull float packagingAT,
            @NotNull float markupCost,
            @NotNull float vat,
            @NotNull float preliminaryLogistics,
            @NotNull float insuranceCost,
            float height,
            float width,
            float length,
            Float totalNet,
            float preparationTime,
            String tag,
            String notes
    ) {
        this.packaging = packaging;
        this.numberOfBoxesInCarton = numberOfBoxesInCarton;
        this.numberOfCartonsInStory = numberOfCartonsInStory;
        this.numberOfStoriesInPallet = numberOfStoriesInPallet;
        this.numberOfBoxesInStory = numberOfBoxesInStory;
        this.numberOfBoxesInPallet = numberOfBoxesInPallet;
        this.productionCost = productionCost;
        this.datePurchase = datePurchase;
        this.laborCost = laborCost;
        this.packagingCost = packagingCost;
        this.fuelCost = fuelCost;
        this.transportCost = transportCost;
        this.laborTransportCost = laborTransportCost;
        this.packagingAT = packagingAT;
        this.markupCost = markupCost;
        this.vat = vat;
        this.preliminaryLogistics = preliminaryLogistics;
        this.insuranceCost = insuranceCost;
        this.height = height;
        this.width = width;
        this.length = length;
        this.totalNet = totalNet;
        this.preparationTime = preparationTime;
        this.tag = tag;
        this.notes = notes;
    }

    public float getTotalCosts(){
        return productionCost + datePurchase + laborCost + packagingCost + fuelCost + transportCost + laborTransportCost
                + packagingAT + markupCost + vat + preliminaryLogistics + insuranceCost;
    }
}
