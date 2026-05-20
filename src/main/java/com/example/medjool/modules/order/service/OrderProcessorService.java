package com.example.medjool.modules.order.service;

import com.example.medjool.modules.order.dto.OrderRequestDto;
import com.example.medjool.modules.order.dto.OrderResponseDto;

public interface OrderProcessorService {
    /**     * Creates a new order based on the provided OrderRequestDto.
     *
     * @param orderRequestDto the DTO containing order details
     * @return ResponseEntity containing the created order or an error message
     * @throws Exception if an error occurs during order creation
     */
    OrderResponseDto processOrder(OrderRequestDto orderRequestDto);
}
