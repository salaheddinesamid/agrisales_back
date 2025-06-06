package com.example.medjool.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderUpdateRequestDto {

    private String clientName;
    private List<OrderItemUpdateRequestDto> items;
    private double totalWeight;
    private double totalPrice;
}
