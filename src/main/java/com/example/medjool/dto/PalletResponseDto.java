package com.example.medjool.dto;

import lombok.Data;

@Data
public class PalletResponseDto 
{

    private Integer palletId;
    private float packaging;

    private Integer numberOfBoxesInCarton;

    private Integer numberOfCartonsInStory;

    private Integer numberOfStoriesInPallet;

    private Integer numberOfBoxesInStory;

    private Integer numberOfBoxesInPallet;

    private Float productionCost;
    private Float datePurchase;
    private Float fuelCost;
    private Float laborCost;
    private Float packagingCost;
    private Float transportationCost;
    private Float laborTransportCost;
    private Float packagingAT;
    private Float markUpCost;
    private Float vat;
    private Float preliminaryLogisticsCost;
    private Float insuranceCost;

    private float height;
    private float width;
    private float length;

    private Float totalNet;
    private String tag;
    private String notes;
    private double preparationTime;
}
