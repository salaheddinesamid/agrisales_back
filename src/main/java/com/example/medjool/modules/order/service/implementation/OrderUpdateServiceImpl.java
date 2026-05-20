package com.example.medjool.modules.order.service.implementation;

import com.example.medjool.exception.OrderCannotBeCanceledException;
import com.example.medjool.exception.ProductLowStock;
import com.example.medjool.exception.ProductNotFoundException;
import com.example.medjool.modules.order.dto.*;
import com.example.medjool.modules.order.model.Order;
import com.example.medjool.modules.order.model.OrderHistory;
import com.example.medjool.modules.order.model.OrderItem;
import com.example.medjool.modules.order.model.OrderStatus;
import com.example.medjool.modules.order.repository.OrderHistoryRepository;
import com.example.medjool.modules.order.repository.OrderItemRepository;
import com.example.medjool.modules.order.repository.OrderRepository;
import com.example.medjool.modules.order.service.OrderUpdateService;
import com.example.medjool.modules.production.dto.ProductionRequestDto;
import com.example.medjool.modules.production.dto.ProductionResponseDto;
import com.example.medjool.modules.shipment.service.implementation.ShipmentAdderServiceImpl;
import com.example.medjool.modules.stock.model.Pallet;
import com.example.medjool.modules.stock.model.Product;
import com.example.medjool.modules.stock.repository.PalletRepository;
import com.example.medjool.modules.stock.repository.ProductRepository;
import com.example.medjool.modules.stock.service.implementation.StockQueryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class OrderUpdateServiceImpl implements OrderUpdateService {


    private final OrderRepository orderRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final StockQueryServiceImpl stockQueryService;
    private final PalletRepository palletRepository;
    private final RestTemplate restTemplate;
    private final ShipmentAdderServiceImpl shipmentAdderService;

    public OrderUpdateServiceImpl(OrderRepository orderRepository, OrderHistoryRepository orderHistoryRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository, StockQueryServiceImpl stockQueryService, PalletRepository palletRepository, RestTemplate restTemplate, ShipmentAdderServiceImpl shipmentAdderService) {
        this.orderRepository = orderRepository;
        this.orderHistoryRepository = orderHistoryRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.stockQueryService = stockQueryService;
        this.palletRepository = palletRepository;
        this.restTemplate = restTemplate;
        this.shipmentAdderService = shipmentAdderService;
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrder(Long id, OrderUpdateRequestDto orderUpdateRequestDto) {

        try{
            log.info("Fetching the order from the database with ID: {}", id);
            Order order = orderRepository.findByIdForUpdate(id).orElseThrow(()-> new RuntimeException("Order not found"));


            if (order.getStatus() == OrderStatus.READY_TO_SHIPPED) {
                throw new OrderCannotBeCanceledException("Order cannot be updated at this stage.");
            }

            log.info("Processing the deleted items: {}", orderUpdateRequestDto.getItemsDeleted());
            processItemsDeleted(order,orderUpdateRequestDto.getItemsDeleted());
            log.info("Deleted items processed successfully.");

            log.info("Processing the new items: {}", orderUpdateRequestDto.getItemsAdded());
            processItemsAdded(order,orderUpdateRequestDto.getItemsAdded());
            log.info("New items processed successfully.");

            log.info("Processing the updated items: {}", orderUpdateRequestDto.getUpdatedItems());
            processItemsUpdated(order,orderUpdateRequestDto.getUpdatedItems());
            log.info("Updated items processed successfully.");


            double totalPrice = order.getOrderItems().stream()
                    .map(item -> item.getItemWeight() * item.getPricePerKg())
                    .reduce(0.0, Double::sum);
            double totalWeight = order.getOrderItems().stream()
                    .map(OrderItem::getItemWeight)
                    .reduce(0.0, Double::sum);


            order.setTotalPrice(totalPrice);
            order.setTotalWeight(totalWeight);

            return new OrderResponseDto(order);

        }catch (RuntimeException e){
            throw new RuntimeException("");
        }

    }

    @Override
    public OrderResponseDto updateOrderStatus(Long id, OrderStatusDto orderStatusDto) throws Exception {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            throw new RuntimeException();
        }

        OrderHistory orderHistory = orderHistoryRepository.findByOrderId(order.getId());
        if (orderHistory == null) {
            throw new RuntimeException("Order history not found for this order.");
        }

        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(orderStatusDto.getNewStatus());
        } catch (IllegalArgumentException e) {
           throw new RuntimeException("Invalid status: " + orderStatusDto.getNewStatus());
        }

        OrderStatus currentStatus = order.getStatus();

        // Prevent illegal cancellation
        if (List.of(OrderStatus.IN_PRODUCTION, OrderStatus.READY_TO_SHIPPED, OrderStatus.SHIPPED).contains(currentStatus)
                && newStatus == OrderStatus.CANCELED) {
            throw new OrderCannotBeCanceledException("Order cannot be canceled at this stage.");
        }

        switch (newStatus) {
            case CONFIRMED -> {
                order.setStatus(OrderStatus.CONFIRMED);
                order.setProductionDate(orderStatusDto.getPreferredProductionDate());
                orderHistory.setConfirmedAt(LocalDateTime.now());
                orderHistory.setPreferredProductionDate(orderStatusDto.getPreferredProductionDate());

                ProductionRequestDto productionRequestDto = new ProductionRequestDto(
                        order.getId(),
                        order.getProductionDate(),
                        order.getWorkingHours()
                );

                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth("6jQBoznefQ5PeXKj4AcBOWflhb6XV4UcAegQIdti5PLUzz18T2QS1FtgGgX5UQUDtZNpNJUt9NU2XOxiq3gNiZns11Zmvuw5oi8WgNTEW28h9ooK2XVtHCE19TnJMx2"); // Externalize this
                HttpEntity<ProductionRequestDto> requestEntity = new HttpEntity<>(productionRequestDto, headers);

                try {
                    log.info("Sending production request: {}", productionRequestDto);

                    ResponseEntity<ProductionResponseDto> response = restTemplate.exchange(
                            "http://localhost:9090/api/production/push",
                            HttpMethod.POST,
                            requestEntity,
                            new ParameterizedTypeReference<>() {}
                    );

                    log.info("Production response: {}", response.getStatusCode());
                } catch (ResourceAccessException e) {
                    log.error("Connection error: {}", e.getMessage());
                    throw new RuntimeException("Production service unavailable", e);
                } catch (HttpClientErrorException e) {
                    log.error("HTTP error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
                    throw new RuntimeException("Production service rejected request", e);
                } catch (Exception e) {
                    log.error("Unexpected error: ", e);
                    throw new RuntimeException("Unexpected error during production service call", e);
                }
            }

            case CANCELED -> {
                // Restore stock and remove items
                for (OrderItem item : new ArrayList<>(order.getOrderItems())) {
                    Product product = item.getProduct();
                    product.setTotalWeight(product.getTotalWeight() + item.getItemWeight());
                    orderItemRepository.delete(item);
                }
                order.getOrderItems().clear();

                // Remove history if needed
                orderHistoryRepository.delete(orderHistory);
            }

            case SHIPPED -> {
                shipmentAdderService.addShipment(order);
                orderHistory.setShippedAt(LocalDateTime.now());
            }

            case IN_PRODUCTION -> orderHistory.setPreferredProductionDate(LocalDateTime.now());
            case READY_TO_SHIPPED -> orderHistory.setReadyToShipAt(LocalDateTime.now());
            case RECEIVED -> orderHistory.setReceivedAt(LocalDateTime.now());

            default -> throw new IllegalArgumentException("Unhandled status: " + newStatus);
        }

        order.setStatus(newStatus);
        Order savedOrder = orderRepository.save(order);
        return new OrderResponseDto(savedOrder);
    }

    /**
     * Processes items added to an order.
     *
     * @param order      the order to which items are being added
     * @param addedItems the list of items to be added
     */
    @Transactional
    protected void processItemsAdded(Order order, List<OrderItemRequestDto> addedItems) {

        List<OrderItem> items = new ArrayList<>();
        List<Pallet> pallets = palletRepository.findAll();

        List<Product> updatedProducts = new ArrayList<>();
        // Create a map for quick pallet lookup by ID:
        HashMap<Integer,Pallet> palletHashMap = new HashMap<>();
        for(Pallet p : pallets){
            palletHashMap.put(p.getPalletId(), p);
        }

        for (OrderItemRequestDto dto : addedItems) {
            // Fetch product with locking
            Product p = productRepository.findByProductCodeForUpdate(dto.getProductCode())
                    .orElseThrow(ProductNotFoundException::new);

            // Fetch pallet with validation
            Pallet pallet = palletHashMap.get(dto.getPalletId());
            if(pallet == null){
                throw new RuntimeException("Pallet not found with id: " + dto.getPalletId());
            }

            double itemWeight = pallet.getTotalNet() * dto.getNumberOfPallets();

            // Check stock availability
            if (!stockQueryService.validateStock(dto.getProductCode(), itemWeight)) {
                throw new ProductLowStock("Insufficient stock for product: " + p.getProductCode());
            }



            // Update product weight
            p.setTotalWeight(p.getTotalWeight() - itemWeight); // Deduct the weight from the product stock
            updatedProducts.add(p);

            // Map DTO to Entity
            OrderItem newItem = new OrderItem(
                    p,
                    itemWeight,
                    dto.getPricePerKg(),
                    dto.getPackaging(),
                    dto.getNumberOfPallets(),
                    order.getCurrency(),
                    dto.getItemBrand(),
                    pallet,
                    order
            );

            //orderItemRepository.save(newItem);
            items.add(newItem);
            order.addOrderItem(newItem);
        }

        // Save the updated products in a single batch:
        productRepository.saveAll(updatedProducts);

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
    protected void processItemsDeleted(Order order,List<Long> deletedItems){

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
    protected void processItemsUpdated(Order order, List<OrderItemUpdateRequestDto> updatedItems) {

        List<OrderItem> updatedOrderItems = new ArrayList<>();
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
            if (!stockQueryService.validateStock(dto.getProductCode(), newWeight)) {
                throw new ProductLowStock("Insufficient stock for product: " + newProduct.getProductCode());
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
            updatedOrderItems.add(orderItem);
        }

        // Save all the update items in one batch:
        orderItemRepository.saveAll(updatedOrderItems);
        orderRepository.save(order);
    }
}
