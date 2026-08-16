package com.example.medjool.modules.order.controller;

import com.example.medjool.modules.order.dto.*;
import com.example.medjool.modules.order.service.OrderProcessorService;
import com.example.medjool.modules.order.service.implementation.OrderCancellerServiceImpl;
import com.example.medjool.modules.order.service.implementation.OrderQueryServiceImpl;
import com.example.medjool.modules.order.service.implementation.OrderUpdateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/** * Controller for handling order-related requests such as creating, updating, and retrieving orders.
 */


@RestController
@RequestMapping("api/order")
public class OrderController {

    
    private final OrderQueryServiceImpl orderQueryService;
    private final List<OrderProcessorService> orderProcessorServices;
    private final OrderUpdateServiceImpl orderUpdateService;
    private final OrderCancellerServiceImpl orderCancellerService;

    @Autowired
    public OrderController(OrderQueryServiceImpl orderQueryService, List<OrderProcessorService> orderProcessorServices, OrderUpdateServiceImpl orderUpdateService, OrderCancellerServiceImpl orderCancellerService) {
        this.orderQueryService = orderQueryService;
        this.orderProcessorServices = orderProcessorServices;
        this.orderUpdateService = orderUpdateService;
        this.orderCancellerService = orderCancellerService;
    }


    /**     * Creates a new order based on the provided order request data.
     *
     * @param orderRequestDto the order request data containing product details and quantities
     * @return ResponseEntity with the result of the order creation
     * @throws Exception if there is an error during order creation
     */
    @PostMapping("")
    public ResponseEntity<?> makeOrder(@RequestBody OrderRequestDto orderRequestDto) throws Exception {
        try{
            OrderProcessorService processor = orderProcessorServices
                    .stream().filter(orderProcessorService -> orderProcessorService.supports("REGULAR"))
                    .findFirst().get();

            return ResponseEntity.status(200)
                    .body(processor.processOrder(orderRequestDto));

        }
        catch (Exception exception){
            return ResponseEntity.status(500)
                    .body("An error occurred during order processing, please try again");
        }
    }


    /**     * Retrieves all the orders.
     *
     * @return OrderResponseDto containing details of the specified order
     */
    @GetMapping("/get_all")
    public ResponseEntity<?> getAll(){
        try{
            return ResponseEntity.ok(orderQueryService.getAllOrders());
        }catch (Exception e){
            return ResponseEntity.status(500)
                    .body("An error occurred during fetching the orders...");
        }
    }

    /**     * Updates an order status by its ID.
     *
     * @param id the ID of the order to update
     * @return OrderResponseDto containing details of the specified order
     */
    @PutMapping("status/update/{id}")
    public ResponseEntity<Object> updateOrderStatus(@PathVariable Long id, @RequestBody OrderStatusDto orderStatusDto) throws Exception {
        try{
            return ResponseEntity.ok(
                    orderUpdateService.updateOrderStatus(id,orderStatusDto)
            );
        }catch (Exception e){
            return ResponseEntity.status(500)
                    .body("");
        }
    }

    /**     * Updates an order details by its ID.
     *
     * @param id the ID of the order to update
     * @return OrderResponseDto containing details of the specified order
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody OrderUpdateRequestDto orderRequestDto) throws Exception {
        try{
            return ResponseEntity.ok(
                    orderUpdateService.updateOrder(id,orderRequestDto)
            );
        }catch (Exception exception){
            return ResponseEntity.status(500)
                    .body("An error occurred during order update");
        }
    }


    /**     * Retrieves all order histories.
     *
     * @return OrderResponseDto containing details of the specified order
     */
    @GetMapping("history/get_all")
    public ResponseEntity<?> getAllHistory(){
        try{
            return ResponseEntity.ok(
                    orderQueryService.getAllOrderHistory()
            );
        }catch (Exception exception){
            return ResponseEntity.status(500)
                    .body("An error occurred during fetching order history");
        }
    }

    /**     * Cancels an order by its ID.
     *
     * @param id the ID of the order to cancel
     * @return ResponseEntity indicating the result of the cancellation
     */
    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<Object> cancelOrder(@PathVariable Long id) {
        try{
            orderCancellerService.cancelOrder(id);
            return ResponseEntity.status(200)
                    .body(String.format("The order with ID: %s has been cancelled", id));
        }catch (Exception ex){
            return ResponseEntity.status(500)
                    .body("");
        }
    }

    /**     * Retrieves an order by its ID.
     *
     * @param id the ID of the order to retrieve
     * @return OrderResponseDto containing details of the specified order
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id) {
        try{
            return ResponseEntity.ok(
                    orderQueryService.getOrderById(id)
            );
        }catch (Exception exception){
            return ResponseEntity.status(500)
                    .body("");
        }
    }

}
