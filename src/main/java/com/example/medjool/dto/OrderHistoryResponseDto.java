package com.example.medjool.dto;

import com.example.medjool.model.OrderHistory;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderHistoryResponseDto {

    private long historyId;
    private Long orderNumber;
    private String clientName;
    private LocalDateTime confirmedAt;
    private LocalDateTime sentToProductionAt;
    private LocalDateTime inProductionAt;
    private LocalDateTime readyToShipAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime receivedAt;


    public OrderHistoryResponseDto(OrderHistory orderHistory) {
        this.historyId = orderHistory.getId();
        this.orderNumber = orderHistory.getOrder().getId();
        this.clientName = orderHistory.getOrder().getClient().getCompanyName();
        this.confirmedAt = orderHistory.getConfirmedAt();
        this.sentToProductionAt = orderHistory.getPreferredProductionDate();
        this.inProductionAt = orderHistory.getPreferredProductionDate();
        this.readyToShipAt = orderHistory.getReadyToShipAt();
        this.shippedAt = orderHistory.getShippedAt();
        this.receivedAt = orderHistory.getReceivedAt();

    }


}
