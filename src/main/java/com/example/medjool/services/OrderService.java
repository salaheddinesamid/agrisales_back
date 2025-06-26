package com.example.medjool.services;
import com.example.medjool.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

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

    /**     * Updates an existing order based on the provided OrderUpdateRequestDto.
     *
     * @param id the ID of the order to update
     * @param orderUpdateRequestDto the DTO containing updated order details
     * @return ResponseEntity containing the updated order or an error message
     */
    ResponseEntity<Object> updateOrder(Long id, OrderUpdateRequestDto orderUpdateRequestDto);


    /**     * Updates the status of an order based on the provided OrderStatusDto.
     *
     * @param id the ID of the order to update
     * @param orderStatusDto the DTO containing the new status for the order
     * @return ResponseEntity containing the updated order status or an error message
     * @throws Exception if an error occurs during status update
     */
    ResponseEntity<Object> updateOrderStatus(Long id, OrderStatusDto orderStatusDto) throws Exception;

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


}