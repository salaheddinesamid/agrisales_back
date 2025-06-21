package com.example.medjool.dto;

import com.example.medjool.model.OrderItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemResponseDto {
    private Long id;
    private Long productId;
    private String productCode;
    private String callibre;
    private String quality;
    private String color;
    private String brand;
    private double pricePerKg;
    private String currency;
    private double packaging;
    private int palletId;
    private int numberOfPallets;
    private double itemWeight;

    public OrderItemResponseDto(OrderItem orderItem) {
        this.id = orderItem.getId();
        this.productId = orderItem.getProduct().getProductId();
        this.productCode = orderItem.getProduct().getProductCode();
        this.brand = orderItem.getBrand();
        this.quality = orderItem.getProduct().getQuality();
        this.color = orderItem.getProduct().getColor();
        this.pricePerKg = orderItem.getPricePerKg();
        this.currency = orderItem.getOrderCurrency().toString();
        this.packaging = orderItem.getPackaging();
        this.callibre = orderItem.getProduct().getCallibre();
        this.numberOfPallets = orderItem.getNumberOfPallets();
        this.itemWeight = orderItem.getItemWeight();
        this.palletId = orderItem.getPallet().getPalletId();
    }
}