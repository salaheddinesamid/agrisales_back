package com.example.medjool.dto;

import lombok.Data;

import java.util.List;

@Data
public class MixedOrderDto {

    private List<MixedOrderItemRequestDto> items;
    private String brand;
    private int palletId;
}
