package com.example.medjool.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderCostDto {

    private Long orderId;
    List<OrderItemCostDto> itemsCost;
    private double totalCost;
    public OrderCostDto(Long id, List<OrderItemCostDto> itemsCost, double totalCost) {
        this.orderId = id;
        this.itemsCost = itemsCost;
        this.totalCost = totalCost;

    }
}
