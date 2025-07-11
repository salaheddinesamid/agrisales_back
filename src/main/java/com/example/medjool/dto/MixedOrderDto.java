package com.example.medjool.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MixedOrderDto {

    private List<MixedOrderItemRequestDto> items;

    @NotNull
    private int palletId;
}
