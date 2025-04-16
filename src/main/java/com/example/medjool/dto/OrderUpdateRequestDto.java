package com.example.medjool.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderUpdateRequestDto {

    private String clientName;
    private List<OrderItemResponseDto> items;
    private double totalWeight;
    private double totalPrice;
}
