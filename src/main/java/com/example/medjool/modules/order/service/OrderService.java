package com.example.medjool.modules.order.service;
import com.example.medjool.modules.order.dto.*;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface OrderService {

    /**     * Creates a new order based on the provided OrderRequestDto.
     *
     * @param orderDto the DTO containing order details
     * @return ResponseEntity containing the created order or an error message
     * @throws Exception if an error occurs during order creation
     */
    ResponseEntity<?> createOrder(OrderRequestDto orderDto) throws Exception;

    /**
     * Retrieves all orders.
     *
     * @return List of OrderResponseDto containing details of all orders
     */
    List<OrderResponseDto> getAllOrders();

    /**
     * Retrieves an order by its ID.
     *
     * @param id the ID of the order to retrieve
     * @return OrderResponseDto containing details of the specified order
     */
    OrderResponseDto getOrderById(Long id);


    ResponseEntity<?> updateOrder(Long id, OrderUpdateRequestDto orderUpdateRequestDto);




    /**     * Cancels an order by its ID.
     *
     * @param id the ID of the order to cancel
     * @return ResponseEntity indicating the result of the cancellation operation
     */
    ResponseEntity<Object> cancelOrder(Long id);

    /**     * Retrieves all order history for a specific order by its ID.
     *
     * @return ResponseEntity containing a list of OrderHistoryResponseDto for the specified order
     */
    ResponseEntity<List<OrderHistoryResponseDto>> getAllOrderHistory();

    String test();

}