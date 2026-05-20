package com.example.medjool.modules.order.service;

import com.example.medjool.modules.order.dto.OrderHistoryResponseDto;
import com.example.medjool.modules.order.dto.OrderResponseDto;

import java.util.List;

public interface OrderQueryService {

    /**
     * Retrieves all orders from the repository.
     *
     * @return List of OrderResponseDto containing order details
     */
    List<OrderResponseDto> getAllOrders();

    /**     * Retrieves an order by its ID.
     *
     * @param id the ID of the order to retrieve
     * @return OrderResponseDto containing order details, or null if not found
     */
    OrderResponseDto getOrderById(Long id);

    List<OrderHistoryResponseDto> getAllOrderHistory();
}
