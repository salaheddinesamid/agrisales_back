package com.example.medjool.services.implementation;

import com.example.medjool.dto.*;
import com.example.medjool.exception.*;
import com.example.medjool.model.*;
import com.example.medjool.repository.*;
import com.example.medjool.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @CacheEvict(value = "marginPerClient", key = "#orderRequest.clientName")
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

        // Store updated products to avoid duplicate database calls:
        Set<Product> updatedProducts = new HashSet<>();

        /**
         * Process each item in the order request
         * - Validate product availability
         * - Update product stock
         * - Create OrderItem and associate it with the Order
         */

        // Fetch the products from the db once:
        List<Product> productsList = productRepository.findAll();

        // Fetch the pallets from the db once:
        List<Pallet> palletList = palletRepository.findAll();

        // Store products by their codes:
        HashMap<String,Product> productHashMap = new HashMap<>();
        for(Product p : productsList){
            productHashMap.put(p.getProductCode(),p);
        }

        // Store the pallets by their ids:
        HashMap<Integer, Pallet> palletHashMap = new HashMap<>();
        for(Pallet p : palletList){
            palletHashMap.put(p.getPalletId(), p);
        }

        List<OrderItem> orderItemsList = new ArrayList<>();

        // Iterate over each order item:
        for (OrderItemRequestDto itemDto : orderRequest.getItems()) {
            Product product = productHashMap.get(itemDto.getProductCode());
            if(product == null){
                throw new ProductNotFoundException();
            }

            if (validateStock(product, itemDto.getItemWeight())) {
                throw new ProductLowStock("The product with code: " + product.getProductCode() + " does not have enough stock.");
            }

            product.setTotalWeight(product.getTotalWeight() - itemDto.getItemWeight());
            updatedProducts.add(product);

            //Avoid duplicate db calls:
            Pallet pallet = palletHashMap.get(itemDto.getPalletId());
            if(pallet == null){
                throw new PalletNotFoundException("The pallet with id: " + itemDto.getPalletId() + " does not exist.");
            }
            OrderItem orderItem = new OrderItem(
                    product,
                    itemDto.getItemWeight(),
                    itemDto.getPricePerKg(),
                    itemDto.getPackaging(),
                    itemDto.getNumberOfPallets(),
                    OrderCurrency.valueOf(itemDto.getCurrency()),
                    itemDto.getItemBrand(),
                    pallet,
                    order
            );
            order.setCurrency(OrderCurrency.valueOf(itemDto.getCurrency()));

            order.getOrderItems().add(orderItem); // maintain bidirectional relationship
            orderItemsList.add(orderItem);
            totalPrice += itemDto.getPricePerKg() * itemDto.getItemWeight();
            totalWeight += itemDto.getItemWeight();
            estimatedDeliveryTime += (long) pallet.getPreparationTime();
            totalWorkingHours += pallet.getPreparationTime();
        }

        //productRepository.saveAll(order.getOrderItems().stream().map(OrderItem::getProduct).collect(Collectors.toList()));

        /** * Process mixed order details
         * - Total price and weight
         * - Production date and status
         * - Shipping address and estimated delivery date
         */
        if(orderRequest.getMixedOrderDto().getItems() != null) {
            MixedOrderDto mixedOrderDto = orderRequest.getMixedOrderDto();
            MixedOrderItem mixedOrderItem = new MixedOrderItem();
            List<MixedOrderItemDetails> mixedOrderItemsDetails = new ArrayList<>();

            Pallet pallet = palletHashMap.get(mixedOrderDto.getPalletId());

            if(pallet == null){
                throw new PalletNotFoundException("The pallet with id: " + mixedOrderDto.getPalletId() + " does not exist.");
            }

            for(MixedOrderItemRequestDto mixedOrderItemRequestDto : mixedOrderDto.getItems()) {

                Product product = productHashMap.get(mixedOrderItemRequestDto.getProductCode());

                if(product == null){
                    throw new ProductNotFoundException();
                }

                double percentageWeight = Double.valueOf(pallet.getTotalNet() * (mixedOrderItemRequestDto.getPercentage() / 100.0));
                System.out.println("Required: " + percentageWeight + ", Available: " + product.getTotalWeight());

                if (validateStock(product, mixedOrderItemRequestDto.getWeight())) {
                    throw new ProductLowStock("The product with code: " + product.getProductCode() + " does not have enough stock.");
                }

                product.setTotalWeight(product.getTotalWeight() - percentageWeight);
                updatedProducts.add(product);

                MixedOrderItemDetails mixedOrderItemDetails = new MixedOrderItemDetails();
                mixedOrderItemDetails.setProduct(product);
                mixedOrderItemDetails.setWeight(percentageWeight);
                mixedOrderItemDetails.setPercentage(mixedOrderItemRequestDto.getPercentage());
                mixedOrderItemsDetails.add(mixedOrderItemDetails);

            }
            // Batch save:
            mixedOrderItemDetailsRepo.saveAll(mixedOrderItemsDetails);
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
        orderItemRepository.saveAll(orderItemsList);
        // Save the update products in a single batch:
        productRepository.saveAll(updatedProducts);


        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setOrder(savedOrder);
        orderHistoryRepository.save(orderHistory);

        OrderResponseDto orderResponseDto = new OrderResponseDto(savedOrder);
        return ResponseEntity.ok(orderResponseDto);

    }

    private boolean validateStock(Product product, double weight){
        return (product.getTotalWeight() < weight);
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

    /**     * Updates an existing order based on the provided ID and update request.
     *
     * @param id the ID of the order to update
     * @param orderUpdateRequestDto the request containing updated order details
     * @return ResponseEntity indicating success or failure
     */
    @Override
    @Transactional
    public ResponseEntity<Object> updateOrder(Long id, OrderUpdateRequestDto orderUpdateRequestDto) {

        try{
            Order order = orderRepository.findByIdForUpdate(id).orElseThrow(()-> new RuntimeException("Order not found"));

            if (order.getStatus() == OrderStatus.READY_TO_SHIPPED) {
                throw new OrderCannotBeCanceledException("Order cannot be updated at this stage.");
            }


            processItemsDeleted(order,orderUpdateRequestDto.getItemsDeleted());
            processItemsAdded(order,orderUpdateRequestDto.getItemsAdded());
            processItemsUpdated(order,orderUpdateRequestDto.getUpdatedItems());

            double totalPrice = order.getOrderItems().stream()
                    .map(item -> item.getItemWeight() * item.getPricePerKg())
                    .reduce(0.0, Double::sum);
            double totalWeight = order.getOrderItems().stream()
                    .map(OrderItem::getItemWeight)
                    .reduce(0.0, Double::sum);


            order.setTotalPrice(totalPrice);
            order.setTotalWeight(totalWeight);

            return new ResponseEntity<>("Order has been updated successfully.", HttpStatus.OK);
        }catch (RuntimeException e){
            throw new RuntimeException("");
        }

    }

    /**     * Processes items added to an order.
     *
     * @param order the order to which items are being added
     * @param addedItems the list of items to be added
     */
    @Transactional
    public void processItemsAdded(Order order, List<OrderItemRequestDto> addedItems) {

        List<OrderItem> items = new ArrayList<>();
        List<Pallet> pallets = palletRepository.findAll();
        // Create a map for quick pallet lookup by ID:
        HashMap<Integer,Pallet> palletHashMap = new HashMap<>();
        for(Pallet p : pallets){
            palletHashMap.put(p.getPalletId(), p);
        }

        for (OrderItemRequestDto dto : addedItems) {
            // Fetch product with locking
            Product p = productRepository.findByProductCodeForUpdate(dto.getProductCode())
                    .orElseThrow(ProductNotFoundException::new);

            // Check stock availability
            if (p.getTotalWeight() < dto.getItemWeight()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + p.getProductCode());
            }

            // Fetch pallet with validation
            Pallet pallet = palletRepository.findById(dto.getPalletId())
                    .orElseThrow(() -> new RuntimeException("Pallet not found with id: " + dto.getPalletId()));

            // Update product weight
            p.setTotalWeight(p.getTotalWeight() - dto.getItemWeight());
            productRepository.save(p);

            // Map DTO to Entity
            OrderItem newItem = new OrderItem(
                    p,
                    dto.getItemWeight(),
                    dto.getPricePerKg(),
                    dto.getPackaging(),
                    dto.getNumberOfPallets(),
                    OrderCurrency.valueOf(dto.getCurrency()),
                    dto.getItemBrand(),
                    pallet,
                    order
            );

            //orderItemRepository.save(newItem);
            items.add(newItem);
            order.addOrderItem(newItem);
        }

        // Save all the items in a single batch:
        orderItemRepository.saveAll(items);
        orderRepository.save(order); // Optional based on cascade config
    }


    /**     * Processes items deleted from an order.
     *
     * @param order the order from which items are being deleted
     * @param deletedItems the list of item IDs to be deleted
     */
    @Transactional
    public void processItemsDeleted(Order order,List<Long> deletedItems){

        List<Product> updatedProducts = new ArrayList<>();
        List<OrderItem> updatedOrderItems = new ArrayList<>();
        for(Long itemId : deletedItems){
            OrderItem orderItem = orderItemRepository.findByIdForUpdate(itemId)
                    .orElseThrow(() -> new RuntimeException("Order item not found"));
            Product product = orderItem.getProduct();
            product.setTotalWeight(product.getTotalWeight() + orderItem.getItemWeight());
            updatedProducts.add(product);
            updatedOrderItems.add(orderItem);

            //productRepository.save(product);
            //orderItemRepository.delete(orderItem);
            order.getOrderItems().removeIf(item -> item.getId().equals(itemId));
        }

        // Save the products and order items in a single batch:
        productRepository.saveAll(updatedProducts);
        orderItemRepository.saveAll(updatedOrderItems);

        // Save the orders:
        orderRepository.save(order);
    }

    /**     * Processes items updated in an order.
     *
     * @param order the order containing items to be updated
     * @param updatedItems the list of updated item details
     */
    @Transactional
    public void processItemsUpdated(Order order, List<OrderItemUpdateRequestDto> updatedItems) {

        for (OrderItemUpdateRequestDto dto : updatedItems) {
            OrderItem orderItem = orderItemRepository.findByIdForUpdate(dto.getItemId())
                    .orElseThrow(() -> new RuntimeException("Order item not found with ID: " + dto.getItemId()));


            Product oldProduct = orderItem.getProduct();
            Product newProduct = productRepository.findByProductCodeForUpdate(dto.getProductCode())
                    .orElseThrow(ProductNotFoundException::new);

            double oldWeight = orderItem.getItemWeight();
            double newWeight = dto.getNewWeight();

            // Revert old product stock
            oldProduct.setTotalWeight(oldProduct.getTotalWeight() + oldWeight);

            productRepository.save(oldProduct);

            // Check if new product has enough stock
            if (newProduct.getTotalWeight() < newWeight) {
                throw new IllegalArgumentException("Insufficient stock for product: " + newProduct.getProductCode());
            }

            // Deduct new weight from new product
            newProduct.setTotalWeight(newProduct.getTotalWeight() - newWeight);
            productRepository.save(newProduct);

            // Update order item fields
            orderItem.setProduct(newProduct);
            orderItem.setItemWeight(newWeight);
            orderItem.setBrand(dto.getNewBrand());
            orderItem.setPricePerKg(dto.getNewPricePerKg());
            orderItem.setNumberOfPallets(dto.getNewNumberOfPallets());
            orderItem.setPackaging(dto.getNewPackaging());
            orderItemRepository.save(orderItem);
        }

        orderRepository.save(order);
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
                List<OrderHistory> history = order.getOrderHistory();
                for (OrderItem orderItem : order.getOrderItems()) {
                    Product product = orderItem.getProduct();
                    product.setTotalWeight(product.getTotalWeight() + orderItem.getItemWeight());
                    order.getOrderItems().remove(orderItem);
                }
                history.forEach(history::remove);
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
                .map(OrderHistoryResponseDto::new)
                .toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
