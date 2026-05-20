package com.example.medjool.modules.production.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class FactoryScheduleResponseDto {
    private LocalDate date;
    private double workingHours;
    private double remainingHours;
    private boolean isAvailable;
    private List<Long> scheduledOrders;
}
