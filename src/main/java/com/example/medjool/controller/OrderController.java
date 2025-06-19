package com.example.medjool.controller;

import com.example.medjool.dto.*;

import com.example.medjool.services.implementation.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** * Controller for handling order-related requests such as creating, updating, and retrieving orders.
 */


@RestController
@RequestMapping("api/order")
public class OrderController {

    
    private final OrderServiceImpl orderService;
    @Autowired
    public OrderController(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }


    /**     * Creates a new order based on the provided order request data.
     *
     * @param orderRequestDto the order request data containing product details and quantities
     * @return ResponseEntity with the result of the order creation
     * @throws Exception if there is an error during order creation
     */
    @PostMapping("")
    public ResponseEntity<?> makeOrder(@RequestBody OrderRequestDto orderRequestDto) throws Exception {
        return orderService.createOrder(orderRequestDto);
    }


    /**     * Retrieves all the orders.
     *
     * @return OrderResponseDto containing details of the specified order
     */
    @GetMapping("/get_all")
    public List<OrderResponseDto> getAll(){
        return orderService.getAllOrders();
    }

    /**     * Updates an order status by its ID.
     *
     * @param id the ID of the order to update
     * @return OrderResponseDto containing details of the specified order
     */
    @PutMapping("status/update/{id}")
    public ResponseEntity<Object> updateOrder(@PathVariable Long id, @RequestBody OrderStatusDto orderStatusDto) throws Exception {
        return orderService.updateOrderStatus(id,orderStatusDto);
    }

    /**     * Updates an order details by its ID.
     *
     * @param id the ID of the order to update
     * @return OrderResponseDto containing details of the specified order
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateOrder(@PathVariable Long id, @RequestBody OrderUpdateRequestDto orderRequestDto) throws Exception {
        return orderService.updateOrder(id,orderRequestDto);
    }


    /**     * Retrieves all order histories.
     *
     * @return OrderResponseDto containing details of the specified order
     */
    @GetMapping("history/get_all")
    public ResponseEntity<List<OrderHistoryResponseDto>> getAllHistory(){
        return orderService.getAllOrderHistory();
    }

    /**     * Cancels an order by its ID.
     *
     * @param id the ID of the order to cancel
     * @return ResponseEntity indicating the result of the cancellation
     */
    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<Object> cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }

}
