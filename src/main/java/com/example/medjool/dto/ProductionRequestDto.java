package com.example.medjool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProductionRequestDto {

    private Long orderId;
    private LocalDateTime productionStartDate;
    private double workingHours;
}
