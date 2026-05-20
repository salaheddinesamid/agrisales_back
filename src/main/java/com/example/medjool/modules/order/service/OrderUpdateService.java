package com.example.medjool.modules.order.service;

import com.example.medjool.modules.order.dto.OrderResponseDto;
import com.example.medjool.modules.order.dto.OrderStatusDto;
import com.example.medjool.modules.order.dto.OrderUpdateRequestDto;
import com.example.medjool.modules.order.model.Order;
import org.springframework.http.ResponseEntity;

public interface OrderUpdateService {
    /**     * Updates an existing order based on the provided OrderUpdateRequestDto.
     *
     * @param id the ID of the order to update
     * @param orderUpdateRequestDto the DTO containing updated order details
     * @return ResponseEntity containing the updated order or an error message
     */
    OrderResponseDto updateOrder(Long id, OrderUpdateRequestDto orderUpdateRequestDto);

    /**     * Updates the status of an order based on the provided OrderStatusDto.
     *
     * @param id the ID of the order to update
     * @param orderStatusDto the DTO containing the new status for the order
     * @return ResponseEntity containing the updated order status or an error message
     * @throws Exception if an error occurs during status update
     */
    OrderResponseDto updateOrderStatus(Long id, OrderStatusDto orderStatusDto) throws Exception;
}
