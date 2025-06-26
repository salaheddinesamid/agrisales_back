package com.example.medjool.dto;

import lombok.Data;

@Data
public class OrderItemUpdateRequestDto {

    private Long itemId;

    private String productCode;
    private double newQuantity;
    private double newPrice;
    private double newPackaging;
    private String newBrand;
    private int newNumberOfPallets;
    private double newWeight;
    private double newPricePerKg;
    private Integer newPalletId;

}
