package com.example.medjool.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductionResponseDto {

    private Long orderId;
    private LocalDateTime productionStartDate;
    private double workingHours;
    private double remainingHours;
    private String status;

}
