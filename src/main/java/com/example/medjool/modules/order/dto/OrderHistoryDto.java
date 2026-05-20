package com.example.medjool.modules.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderHistoryDto {

    private Long orderId;
    private String newStatus;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime preferredProductionDate;
}
