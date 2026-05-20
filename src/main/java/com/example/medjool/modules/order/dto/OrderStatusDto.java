package com.example.medjool.modules.order.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderStatusDto {
    String newStatus;
    LocalDateTime preferredProductionDate;
}
