package com.example.medjool.services.implementation;


import com.example.medjool.dto.MarginClientResponseDto;
import com.example.medjool.dto.OrderCostDto;
import com.example.medjool.dto.OrderItemCostDto;
import com.example.medjool.dto.OverviewDto;
import com.example.medjool.exception.ClientNotFoundException;
import com.example.medjool.model.*;
import com.example.medjool.repository.ClientRepository;
import com.example.medjool.repository.OrderRepository;
import com.example.medjool.repository.ProductRepository;
import com.example.medjool.repository.SystemSettingRepository;
import com.example.medjool.services.OverviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OverviewServiceImpl implements OverviewService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final SystemSettingRepository systemSettingRepository;
    private final AlertServiceImpl alertService;
    private final ClientRepository clientRepository;

    @Autowired
    public OverviewServiceImpl(ProductRepository productRepository, OrderRepository orderRepository, SystemSettingRepository systemSettingRepository, AlertServiceImpl alertService, ClientRepository clientRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.systemSettingRepository = systemSettingRepository;
        this.alertService = alertService;
        this.clientRepository = clientRepository;
    }


    /** * Retrieves an overview of the system, including total stock, orders, and revenue.
     *
     * @return ResponseEntity containing OverviewDto with system overview data
     */
    @Override
    public ResponseEntity<?> getOverview() {
        final String STOCK_KEY = "min_stock_level";

        // Defensive check for system setting
        double minimumStockValue = systemSettingRepository.findByKey(STOCK_KEY)
                .map(SystemSetting::getValue)
                .orElseThrow(() -> new IllegalStateException("System setting 'min_stock_level' not found"));

        List<Product> products = productRepository.findAll();
        double totalStockWeight = products.stream()
                .mapToDouble(product -> {
                    double weight = product.getTotalWeight();
                    if (weight <= minimumStockValue) {
                        String alert = String.format("The product: %s is below the minimum stock level of %.2f", product.getProductId(), minimumStockValue);
                        alertService.newAlert(alert);
                    }
                    return weight;
                }).sum();



        List<Order> orders = orderRepository.findAll();
        long totalOrders = orders.size();


        double totalOrdersPreProduction = orders.stream().map(order -> {
            if(order.getStatus().equals(OrderStatus.PRELIMINARY) ||
                    order.getStatus().equals(OrderStatus.CONFIRMED) ||
                    order.getStatus().equals(OrderStatus.IN_PRODUCTION)) {
                return 1.0; // Count pre-production orders
            }
            return 0.0;
        }).reduce(0.0, Double::sum);

        double totalOrdersPostProduction = orders.stream().map(order -> {
            if(order.getStatus().equals(OrderStatus.READY_TO_SHIPPED)) {
                return 1.0; // Count pre-production orders
            }
            return 0.0;
        }).reduce(0.0, Double::sum);;

        long totalShippedOrders = orders.stream().map(order -> {
            if (order.getStatus().equals(OrderStatus.SHIPPED)) {
                return 1L; // Count shipped orders
            }
            return 0L; // Not a shipped order
        }).reduce(0L, Long::sum);
        double totalShippedRevenue = orders.stream().map(order -> {
            if (order.getStatus().equals(OrderStatus.SHIPPED)) {
                return order.getTotalPrice() * order.getForex().getBuyingRate();
            }
            return 0.0; // Not a shipped order
        }).reduce(0.0, Double::sum);
        double totalPreProductionRevenue = orders.stream().map(order -> {
            if(order.getStatus().equals(OrderStatus.PRELIMINARY) ||
                    order.getStatus().equals(OrderStatus.CONFIRMED) ||
                    order.getStatus().equals(OrderStatus.IN_PRODUCTION)) {
                return order.getTotalPrice() * order.getForex().getBuyingRate();
            }
            return 0.0;
        }).reduce(0.0, Double::sum);;
        double totalPostProductionRevenue = orders.stream().map(order -> {
            if(order.getStatus().equals(OrderStatus.READY_TO_SHIPPED)) {
                return order.getTotalPrice() * order.getForex().getBuyingRate();
            }
            return 0.0;
        }).reduce(0.0, Double::sum);

        double totalRevenue = totalPreProductionRevenue + totalPostProductionRevenue + totalShippedRevenue;

        OverviewDto overviewDto = new OverviewDto(
                totalStockWeight,
                totalOrders,
                totalOrdersPreProduction,
                totalOrdersPostProduction,
                totalShippedOrders,
                totalPreProductionRevenue,
                totalPostProductionRevenue,
                totalShippedRevenue,
                totalRevenue
        );
       return new ResponseEntity<>(overviewDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MarginClientResponseDto> getMarginPerClient(String companyName, String quality) {
        MarginClientResponseDto response = clientMargin(companyName, quality);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /** * Retrieves the margin for all clients for a specific product code.
     *
     * @param productCode the product code to filter by, or "all" for all products
     * @return ResponseEntity containing a list of MarginClientResponseDto for each client
     */
    @Override
    public ResponseEntity<?> getAllMarginPerClient(String productCode) {
        List<MarginClientResponseDto> allMargins = clientRepository.findAll()
                .stream()
                .map(client -> getMarginPerClient(client.getCompanyName(), productCode).getBody())
                .toList();

        return new ResponseEntity<>(allMargins, HttpStatus.OK);
    }

    /** * Retrieves the margin for a specific client based on company name and product code.
     *
     * @param companyName the name of the company
     * @param productCode the product code to filter by, or "all" for all products
     * @return MarginClientResponseDto containing margin details for the client
     */
    private MarginClientResponseDto clientMargin(String companyName, String productCode){
        Client client = clientRepository.findByCompanyName(companyName);
        if(client == null) {
            throw new ClientNotFoundException();
        }
        List<Order> clientOrders = orderRepository.findAllByClient(client);
        List<Order> filteredOrders = clientOrders.stream().filter(order -> {
            if(!productCode.equals("all")) {
                return order.getOrderItems().stream().anyMatch(item -> item.getProduct().getProductCode().equals(productCode));
            }
            return true;
        }).toList();

        if(clientOrders.isEmpty()) {
            return new MarginClientResponseDto(
                    companyName,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0
            );
        }

        List<OrderCostDto> ordersCost = filteredOrders
                .stream().map(order -> {
                    List<OrderItem> items = order.getOrderItems();
                    List<OrderItemCostDto> itemsCosts = items
                            .stream().map(item -> {
                                Pallet pallet = item.getPallet();
                                return new OrderItemCostDto(pallet, item.getNumberOfPallets());
                            }).toList();
                    double orderTotalCost = itemsCosts.stream()
                            .map(OrderItemCostDto::getTotal)
                            .reduce(0.0, Double::sum);

                    return new OrderCostDto(order.getId(),itemsCosts,orderTotalCost);
                }).toList();

        double totalWeight = filteredOrders.stream()
                .map(Order::getTotalWeight)
                .reduce(0.0, Double::sum);
        double totalRevenue = filteredOrders.stream()
                .map(clientOrder->{
                    if(clientOrder.getCurrency().equals(OrderCurrency.USD)) {
                        return clientOrder.getTotalPrice() * 10.5;
                    } else if(clientOrder.getCurrency().equals(OrderCurrency.EUR)) {
                        return clientOrder.getTotalPrice() * 11;
                    } else if(clientOrder.getCurrency().equals(OrderCurrency.MAD)) {
                        return clientOrder.getTotalPrice();
                    }
                    return 0.0;
                })
                .reduce(0.0, Double::sum);

        double totalOrdersCost = ordersCost.stream()
                .map(OrderCostDto::getTotalCost)
                .reduce(0.0, Double::sum);

        double marginOnVariableCost = totalRevenue - totalOrdersCost;
        double margin =  marginOnVariableCost / totalWeight;

                return new MarginClientResponseDto(
                        companyName,
                        totalWeight,
                        totalRevenue,
                        totalOrdersCost,
                        marginOnVariableCost,
                        margin
                );
    }


}
