package com.example.medjool.dto;

import lombok.Data;

@Data
public class UpdateForexDto {

    private String currency;
    private double buyingRate;
}
