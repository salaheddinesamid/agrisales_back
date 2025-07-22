package com.example.medjool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewProductDto {
    private String productCode;
    private String callibre;
    private String quality;
    private String farm;
    private double totalWeight;
}
