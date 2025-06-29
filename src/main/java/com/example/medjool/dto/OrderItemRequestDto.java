package com.example.medjool.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemRequestDto {

    private String productCode;
    private double pricePerKg;
    private double packaging;
    private int numberOfPallets;
    private double itemWeight;
    private String itemBrand;
    private Integer palletId;
    private String currency;

}