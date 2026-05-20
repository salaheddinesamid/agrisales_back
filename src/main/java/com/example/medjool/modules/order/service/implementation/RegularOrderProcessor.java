package com.example.medjool.modules.order.service.implementation;

import com.example.medjool.exception.*;
import com.example.medjool.modules.settings.model.Forex;
import com.example.medjool.modules.settings.model.ForexCurrency;
import com.example.medjool.modules.client.model.Client;
import com.example.medjool.modules.client.model.ClientStatus;
import com.example.medjool.modules.client.repository.ClientRepository;
import com.example.medjool.modules.order.dto.OrderItemRequestDto;
import com.example.medjool.modules.order.dto.OrderRequestDto;
import com.example.medjool.modules.order.dto.OrderResponseDto;
import com.example.medjool.modules.order.model.*;
import com.example.medjool.modules.order.repository.OrderHistoryRepository;
import com.example.medjool.modules.order.repository.OrderRepository;
import com.example.medjool.modules.order.service.OrderProcessorService;
import com.example.medjool.modules.stock.model.Pallet;
import com.example.medjool.modules.stock.model.Product;
import com.example.medjool.modules.stock.repository.PalletRepository;
import com.example.medjool.modules.stock.repository.ProductRepository;
import com.example.medjool.modules.stock.service.implementation.StockQueryServiceImpl;
import com.example.medjool.modules.user_management.service.implementation.repository.ForexRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RegularOrderProcessor implements OrderProcessorService {

    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final StockQueryServiceImpl stockQueryService;
    private final PalletRepository palletRepository;
    private final ForexRepository forexRepository;
    private final OrderHistoryRepository orderHistoryRepository;

    public RegularOrderProcessor(ClientRepository clientRepository, ProductRepository productRepository, OrderRepository orderRepository, StockQueryServiceImpl stockQueryService, PalletRepository palletRepository, ForexRepository forexRepository, OrderHistoryRepository orderHistoryRepository) {
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.stockQueryService = stockQueryService;
        this.palletRepository = palletRepository;
        this.forexRepository = forexRepository;
        this.orderHistoryRepository = orderHistoryRepository;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @CacheEvict(value = "marginPerClient", key = "#orderRequestDto.clientName")
    public OrderResponseDto processOrder(OrderRequestDto orderRequestDto) {
        log.info("New Order is being processed...");
        log.info(orderRequestDto.toString());

        // Validate and fetch client
        Client client = Optional.ofNullable(clientRepository.findByCompanyName(orderRequestDto.getClientName()))
                .filter(c -> c.getClientStatus() == ClientStatus.ACTIVE)
                .orElseThrow(ClientNotActiveException::new); // Throw an exception when a client is not found

        Order order = new Order();
        List<OrderItem> orderItems = new ArrayList<>();
        order.setClient(client);
        order.setOrderItems(new ArrayList<>());

        Set<Product> updatedProducts = new HashSet<>();
        List<OrderItem> orderItemsList = new ArrayList<>();

        List<Product> productsList = productRepository.findAll();
        List<Pallet> palletList = palletRepository.findAll();

        // Map products by product code for quick lookup:
        Map<String, Product> productMap = productsList.stream()
                .collect(Collectors.toMap(Product::getProductCode, p -> p));

        // Map pallets by ID for quick lookup:
        Map<Integer, Pallet> palletMap = palletList.stream()
                .collect(Collectors.toMap(Pallet::getPalletId, p -> p));

        // Fetch forex from the database:
        Forex forex = forexRepository.findByCurrency(ForexCurrency.valueOf(orderRequestDto.getCurrency()))
                .orElseThrow(() -> new ForexNotFoundException("The forex is not found: "+ orderRequestDto.getCurrency())); // handle case where forex is not found

        String orderCurrency = orderRequestDto.getCurrency();
        order.setForex(forex);

        /**         * Process Regular Order Items
         * This will handle the regular items in the order request.
         */
        for (OrderItemRequestDto itemDto : orderRequestDto.getItems()) {
            Product product = productMap.get(itemDto.getProductCode());

            Pallet pallet = palletMap.get(itemDto.getPalletId());
            if (pallet == null) throw new PalletNotFoundException("Pallet ID " + itemDto.getPalletId() + " not found.");

            double itemWeight = pallet.getTotalNet() * itemDto.getNumberOfPallets(); // Item weight to be calculated based on pallet weight and number of pallets
            if (product == null) throw new ProductNotFoundException();

            if (!stockQueryService.validateStock(itemDto.getProductCode(), itemWeight)) {
                throw new ProductLowStock("Product " + product.getProductCode() + " has insufficient stock.");
            }

            product.setTotalWeight(product.getTotalWeight() - itemWeight); // Update product weight in the stock
            updatedProducts.add(product); // Add the updated product into a list

            OrderItem orderItem = new OrderItem(
                    product,
                    itemWeight,
                    itemDto.getPricePerKg(),
                    itemDto.getPackaging(),
                    itemDto.getNumberOfPallets(),
                    OrderCurrency.valueOf(orderCurrency),
                    itemDto.getItemBrand(),
                    pallet,
                    order
            );

            order.setCurrency(OrderCurrency.valueOf(orderCurrency));
            orderItems.add(orderItem);
            order.getOrderItems().add(orderItem);
        }

        // Calculate total price and weight
        double totalRegularItemPrice =
                order.getOrderItems().stream().map(item -> item.getPricePerKg() * item.getItemWeight()).reduce(0.0, Double::sum);

        double totalRegularItemWeight =
                order.getOrderItems().stream().map(OrderItem::getItemWeight).reduce(0.0, Double::sum);

        double totalMixedItemPrice = Optional.ofNullable(order.getMixedOrderItem())
                .map(MixedOrderItem::getItemDetails)
                .map(details -> details.stream()
                        .mapToDouble(item -> item.getPricePerKg() * item.getWeight())
                        .sum())
                .orElse(0.0);

        double totalMixedItemWeight = Optional.ofNullable(order.getMixedOrderItem())
                .map(MixedOrderItem::getItemDetails)
                .map(details -> details.stream()
                        .mapToDouble(MixedOrderItemDetails::getWeight)
                        .sum())
                .orElse(0.0);


        double totalPrice = totalRegularItemPrice + totalMixedItemPrice;
        double totalWeight = totalRegularItemWeight + totalMixedItemWeight;
        order.setTotalPrice(totalPrice);
        order.setTotalWeight(totalWeight);

        double totalWorkingHours = order.getOrderItems().stream()
                .mapToDouble(orderItem -> orderItem.getPallet().getPreparationTime())
                .sum();


        long estimatedDeliveryHours = (long) totalWorkingHours;


        order.setProductionDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PRELIMINARY);
        order.setShippingAddress(orderRequestDto.getShippingAddress());
        order.setDeliveryDate(LocalDateTime.now().plusHours(estimatedDeliveryHours));
        order.setWorkingHours(totalWorkingHours);
        order.setOrderDate(LocalDate.now());

        // Save everything at once (cascade assumed)
        Order savedOrder = orderRepository.save(order); // This will save MixedOrderItem and OrderItems if cascading is set

        productRepository.saveAll(updatedProducts);

        OrderHistory history = new OrderHistory();
        history.setOrder(savedOrder);
        orderHistoryRepository.save(history);

        return new OrderResponseDto(savedOrder);
    }
}
