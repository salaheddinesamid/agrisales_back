package com.example.medjool.services.implementation;

import com.example.medjool.dto.*;
import com.example.medjool.exception.ClientNotActiveException;
import com.example.medjool.exception.OrderCannotBeCanceledException;
import com.example.medjool.exception.ProductLowStock;
import com.example.medjool.exception.ProductNotFoundException;
import com.example.medjool.model.*;
import com.example.medjool.repository.*;
import com.example.medjool.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{


    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final ClientRepository clientRepository;
    private final PalletRepository palletRepository;
    private final ShipmentServiceImpl shipmentService;
    private final OrderItemRepository orderItemRepository;
    private final MixedOrderItemRepo mixedOrderItemRepo;
    private final MixeOrderItemDetailsRepo mixedOrderItemDetailsRepo;

    private final RestTemplate restTemplate;

    @Value("${production.service.url}")
    private static String PRODUCTION_SERVICE_URL;


    @Value("${production.service.api.key}")
    private static String API_KEY;

    Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);


    /**
     * Creates a new order based on the provided order request.
     *
     * @param orderRequest the order request containing client and item details
     * @return ResponseEntity indicating success or failure
     */
    @Override
    @Transactional
    public ResponseEntity<?> createOrder(OrderRequestDto orderRequest) {
        logger.info("New Order is being processed...");
        logger.info(orderRequest.toString());

        // Validate client
        Client client = Optional.ofNullable(clientRepository.findByCompanyName(orderRequest.getClientName()))
                .filter(c -> c.getClientStatus() == ClientStatus.ACTIVE)
                .orElseThrow(ClientNotActiveException::new);

        Order order = new Order();
        order.setClient(client);
        order.setOrderItems(new ArrayList<>()); // initialize to prevent null issues

        double totalPrice = 0.0;
        double totalWeight = 0.0;
        long estimatedDeliveryTime = 0;
        double totalWorkingHours = 0;

        /**
         * Process each item in the order request
         * - Validate product availability
         * - Update product stock
         * - Create OrderItem and associate it with the Order
         */
        for (OrderItemRequestDto itemDto : orderRequest.getItems()) {
            Product product = productRepository.findByProductCode(itemDto.getProductCode())
                    .orElseThrow(ProductNotFoundException::new);

            if (product.getTotalWeight() < itemDto.getItemWeight()) {
                throw new ProductLowStock();
            }

            product.setTotalWeight(product.getTotalWeight() - itemDto.getItemWeight());

            Pallet pallet = palletRepository.findById(itemDto.getPalletId())
                    .orElseThrow(() -> new RuntimeException("Pallet not found"));

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setItemWeight(itemDto.getItemWeight());
            orderItem.setPricePerKg(itemDto.getPricePerKg());
            orderItem.setPackaging(itemDto.getPackaging());
            orderItem.setNumberOfPallets(itemDto.getNumberOfPallets());
            orderItem.setOrderCurrency(OrderCurrency.valueOf(itemDto.getCurrency()));
            order.setCurrency(OrderCurrency.valueOf(itemDto.getCurrency()));
            orderItem.setPallet(pallet);
            orderItem.setOrder(order); // maintain bidirectional relationship

            order.getOrderItems().add(orderItem); // maintain bidirectional relationship

            totalPrice += itemDto.getPricePerKg() * itemDto.getItemWeight();
            totalWeight += itemDto.getItemWeight();
            estimatedDeliveryTime += (long) pallet.getPreparationTime();
            totalWorkingHours += pallet.getPreparationTime();
        }

        productRepository.saveAll(order.getOrderItems().stream().map(OrderItem::getProduct).collect(Collectors.toList()));

        /** * Process mixed order details
         * - Total price and weight
         * - Production date and status
         * - Shipping address and estimated delivery date
         */
        if(orderRequest.getMixedOrderDto() != null) {
            MixedOrderDto mixedOrderDto = orderRequest.getMixedOrderDto();
            MixedOrderItem mixedOrderItem = new MixedOrderItem();
            List<MixedOrderItemDetails> mixedOrderItemsDetails = new ArrayList<>();

            Pallet pallet = palletRepository.findById(mixedOrderDto.getPalletId())
                    .orElseThrow(() -> new RuntimeException("Pallet not found"));

            for(MixedOrderItemRequestDto mixedOrderItemRequestDto : mixedOrderDto.getItems()) {

                Product product = productRepository.findByProductCode(mixedOrderItemRequestDto.getProductCode())
                        .orElseThrow(ProductNotFoundException::new);

                double percentageWeight = Double.valueOf(pallet.getTotalNet() * (mixedOrderItemRequestDto.getPercentage() / 100.0));
                System.out.println("Required: " + percentageWeight + ", Available: " + product.getTotalWeight());

                if (product.getTotalWeight() < percentageWeight) {
                    throw new ProductLowStock();
                }

                product.setTotalWeight(product.getTotalWeight() - percentageWeight);

                MixedOrderItemDetails mixedOrderItemDetails = new MixedOrderItemDetails();

                mixedOrderItemDetails.setProduct(product);
                mixedOrderItemDetails.setWeight(percentageWeight);
                mixedOrderItemDetails.setPercentage(mixedOrderItemRequestDto.getPercentage());

                mixedOrderItemsDetails.add(mixedOrderItemDetails);
                mixedOrderItemDetailsRepo.save(mixedOrderItemDetails);

            }
            mixedOrderItem.setItemDetails(mixedOrderItemsDetails);
            order.setMixedOrderItem(mixedOrderItem);
        }

        order.setTotalPrice(totalPrice);
        order.setTotalWeight(totalWeight);
        order.setProductionDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PRELIMINARY);

        order.setShippingAddress(orderRequest.getShippingAddress());
        order.setDeliveryDate(LocalDateTime.now().plusHours(estimatedDeliveryTime));
        order.setWorkingHours(totalWorkingHours);

        order.setOrderDate(LocalDate.now());

        Order savedOrder = orderRepository.save(order); // Save after everything is set
        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setOrder(savedOrder);
        orderHistoryRepository.save(orderHistory);

        return ResponseEntity.ok("Order has been created successfully.");

    }

    /**
     * Retrieves all orders from the repository.
     *
     * @return List of OrderResponseDto containing order details
     */
    @Transactional(readOnly = true)
    @Override
    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderResponseDto::new)
                .collect(Collectors.toList());
    }


    /**     * Retrieves an order by its ID.
     *
     * @param id the ID of the order to retrieve
     * @return OrderResponseDto containing order details, or null if not found
     */
    @Transactional(readOnly = true)
    @Override
    public OrderResponseDto getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(OrderResponseDto::new)
                .orElse(null);
    }


    /**     * Updates an existing order with new details.
     *
     * @param id the ID of the order to update
     * @param orderUpdateRequestDto the new details for the order
     * @return ResponseEntity indicating success or failure
     */
    @Override
    @Transactional
    public ResponseEntity<Object> updateOrder(Long id, OrderUpdateRequestDto orderUpdateRequestDto) {
        Optional<Order> order = orderRepository.findById(id);
        order.ifPresent(o -> {
            if(o.getStatus() == OrderStatus.READY_TO_SHIPPED){
                throw new OrderCannotBeCanceledException("Order cannot be updated at this stage.");
            }

            // Calculate the new delivery date:
            double workingHours = 0;

            for (OrderItemUpdateRequestDto itemRequest : orderUpdateRequestDto.getItems()) {
                Product product = productRepository.findByProductCode(itemRequest.getProductCode()).get();
                Optional<Pallet> pallet = palletRepository.findById(itemRequest.getNewPalletId());
                // Skip if product not found or insufficient stock
                if (product.getTotalWeight() < itemRequest.getNewWeight()){
                    throw new ProductLowStock();
                }
                // Update product inventory
                logger.info("The product with id : {}is being updated...", product.getProductId());
                product.setTotalWeight(product.getTotalWeight() - itemRequest.getNewWeight());
                productRepository.save(product);

                // Incrementing the working hours:
                workingHours += pallet.get().getPreparationTime();
                // Update order item
                OrderItem orderItem = orderItemRepository.findById(itemRequest.getItemId()).get();
                orderItem.setProduct(product);
                orderItem.setPricePerKg(itemRequest.getNewPrice());
                orderItem.setPackaging(itemRequest.getNewPackaging());
                orderItem.setNumberOfPallets(itemRequest.getNewPalletId());
                orderItem.setItemWeight(itemRequest.getNewWeight());
                orderItem.setPallet(pallet.get());
            }
            // Set the new total price
            o.setTotalWeight(orderUpdateRequestDto.getTotalWeight());
            o.setTotalPrice(orderUpdateRequestDto.getTotalPrice());

            // Update the production date:
            LocalDateTime prod_date = LocalDateTime.now();
            o.setProductionDate(prod_date);
            // Update the new delivery date:
            LocalDateTime newDeliveryDate = LocalDateTime.now().plusHours((long) workingHours);
            o.setDeliveryDate(newDeliveryDate);
        });
        return new ResponseEntity<>("Order has been updated.", HttpStatus.OK);
    }


    /**     * Updates the status of an order.
     *
     * @param id the ID of the order to update
     * @param orderStatusDto the new status and optional production date
     * @return ResponseEntity indicating success or failure
     * @throws Exception if the order cannot be found or if the status is invalid
     */
    @Transactional
    @Override
    public ResponseEntity<Object> updateOrderStatus(Long id, OrderStatusDto orderStatusDto) throws Exception {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        OrderHistory orderHistory = orderHistoryRepository.findByOrderId(order.getId());

        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(orderStatusDto.getNewStatus());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status: " + orderStatusDto.getNewStatus());
        }

        OrderStatus currentStatus = order.getStatus();

        // Prevent illegal cancellations
        if (List.of(OrderStatus.IN_PRODUCTION, OrderStatus.READY_TO_SHIPPED, OrderStatus.SHIPPED).contains(currentStatus)
                && newStatus == OrderStatus.CANCELED) {
            throw new OrderCannotBeCanceledException("Order cannot be canceled at this stage.");
        }


        // Status-specific logic
        switch (newStatus) {
            case CONFIRMED -> {
                order.setStatus(OrderStatus.CONFIRMED);
                orderHistory.setConfirmedAt(LocalDateTime.now());
                orderHistory.setPreferredProductionDate(orderStatusDto.getPreferredProductionDate());

                ProductionRequestDto productionRequestDto = new ProductionRequestDto(
                        order.getId(),
                        order.getProductionDate(),
                        order.getWorkingHours()
                );


                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth("6jQBoznefQ5PeXKj4AcBOWflhb6XV4UcAegQIdti5PLUzz18T2QS1FtgGgX5UQUDtZNpNJUt9NU2XOxiq3gNiZns11Zmvuw5oi8WgNTEW28h9ooK2XVtHCE19TnJMx2");
                HttpEntity<ProductionRequestDto> requestEntity = new HttpEntity<>(productionRequestDto, headers);
                try {
                    // Add debug logging
                    logger.info("Sending request to {} with headers: {}", PRODUCTION_SERVICE_URL, headers);
                    logger.info("Request body: {}", productionRequestDto);

                    ResponseEntity<ProductionResponseDto> response = restTemplate.exchange(
                            "http://localhost:9090/api/production/push",
                            HttpMethod.POST,
                            requestEntity,
                            new ParameterizedTypeReference<>() {
                            }
                    );

                    logger.info("Production service response: {}", response.getStatusCode());
                    logger.debug("Response body: {}", response.getBody());

                } catch (ResourceAccessException e) {
                    // Handle connection failures
                    logger.error("Failed to connect to production service: {}", e.getMessage());
                    throw new RuntimeException("Production service unavailable", e);
                } catch (HttpClientErrorException e) {
                    // Handle 4xx errors
                    logger.error("Production service rejected request: {} - {}",
                            e.getStatusCode(), e.getResponseBodyAsString());
                    throw new RuntimeException("Production service error", e);
                } catch (Exception e) {
                    logger.error("Unexpected error", e);
                    throw new RuntimeException("Failed to push order", e);
                }
            }

            case CANCELED -> {
                for (OrderItem orderItem : order.getOrderItems()) {
                    Product product = orderItem.getProduct();
                    product.setTotalWeight(product.getTotalWeight() + orderItem.getItemWeight());
                }
            }

            case SHIPPED -> {
                shipmentService.createShipment(Optional.of(order));
            }

            case IN_PRODUCTION, READY_TO_SHIPPED, RECEIVED -> {
                // No special business logic needed here (yet), just proceed
                if (newStatus == OrderStatus.IN_PRODUCTION) {
                    orderHistory.setPreferredProductionDate(LocalDateTime.now());
                } else if (newStatus == OrderStatus.READY_TO_SHIPPED) {
                    orderHistory.setReadyToShipAt(LocalDateTime.now());
                } else if (newStatus == OrderStatus.SHIPPED) {
                    orderHistory.setShippedAt(LocalDateTime.now());
                } else if (newStatus == OrderStatus.RECEIVED) {
                    orderHistory.setReceivedAt(LocalDateTime.now());
                }
            }

            default -> throw new IllegalArgumentException("Unhandled status: " + newStatus);
        }

        // Final status update
        order.setStatus(newStatus);
        orderRepository.save(order);
        return ResponseEntity.ok().build();
    }



    /**     * Cancels an order by its ID, restoring stock for the products in the order.
     *
     * @param id the ID of the order to cancel
     * @return ResponseEntity indicating success or failure
     */
    @Transactional
    @Override
    public ResponseEntity<Object> cancelOrder(Long id) {
        Order order = orderRepository.findById(id).orElse(null);

        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        if (order.getStatus() == OrderStatus.IN_PRODUCTION) {
            return new ResponseEntity<>("Order is already in production and cannot be canceled at the moment...", HttpStatus.CONFLICT);
        }
        else if(order.getStatus() == OrderStatus.RECEIVED || order.getStatus() == OrderStatus.CANCELED){
            return new ResponseEntity<>("Order is already received and can not be canceled...", HttpStatus.CONFLICT);
        }

        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            // Assuming getWeight() or quantity is the correct field to restore stock
            product.setTotalWeight(product.getTotalWeight() + orderItem.getItemWeight());
            // No need to call productRepository.save(product); if within @Transactional and managed context
        }

        order.setStatus(OrderStatus.CANCELED);
        // No need to call orderRepository.save(order); for same reason

        return new ResponseEntity<>("Order has been cancelled.", HttpStatus.OK);
    }

    /**     * Retrieves all order history entries that are not yet received.
     *
     * @return ResponseEntity containing a list of OrderHistoryResponseDto
     */
    @Override
    public ResponseEntity<List<OrderHistoryResponseDto>> getAllOrderHistory() {


        List<OrderHistoryResponseDto> response =  orderHistoryRepository.findAll()
                        .stream().filter(orderHistory -> orderHistory.getReceivedAt() == null)
                .map(orderHistory ->{
                    OrderHistoryResponseDto orderHistoryResponseDto = new OrderHistoryResponseDto();
                    orderHistoryResponseDto.setHistoryId(orderHistory.getId());
                    orderHistoryResponseDto.setOrderNumber(orderHistory.getOrder().getId());
                    orderHistoryResponseDto.setClientName(orderHistory.getOrder().getClient().getCompanyName());
                    orderHistoryResponseDto.setConfirmedAt(orderHistory.getConfirmedAt());
                    orderHistoryResponseDto.setInProductionAt(orderHistory.getPreferredProductionDate());
                    orderHistoryResponseDto.setReadyToShipAt(orderHistory.getReadyToShipAt());
                    orderHistoryResponseDto.setShippedAt(orderHistory.getShippedAt());
                    orderHistoryResponseDto.setDeliveredAt(orderHistory.getDeliveredAt());
                    orderHistoryResponseDto.setReceivedAt(orderHistory.getReceivedAt());
                    return orderHistoryResponseDto;
                })
                .toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
