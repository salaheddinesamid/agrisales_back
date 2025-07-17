package com.example.medjool.dto;


import com.example.medjool.model.Product;
import lombok.Data;

@Data
public class ProductResponseDto {

    private Long id;
    private String productCode;

    private String callibre;

    private Double totalWeight;

    private String color;

    private String farm;

    private String quality;

    private String brandName;

    public ProductResponseDto(Product product) {
        this.id = product.getProductId();
        this.productCode = product.getProductCode();
        this.callibre = product.getCallibre();
        this.totalWeight = product.getTotalWeight();
        this.farm = product.getFarm();
        this.quality = product.getQuality();

    }

}
