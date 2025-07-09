  package com.example.medjool.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class MixedOrderItemRequestDto {

    @NotNull
    private String productCode;
    @NotNull
    private double percentage;
    @NotNull
    private double pricePerKg;
    private String brand;
    private double weight;
}
