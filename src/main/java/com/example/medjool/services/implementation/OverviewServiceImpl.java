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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
        OverviewDto overviewDto = new OverviewDto();

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
        double totalOrdersPreProduction = 0;
        double totalOrdersPostProduction = 0;
        long totalReceivedOrders = 0;
        double totalReceivedRevenue = 0;
        double totalPreProductionRevenue = 0;
        double totalPostProductionRevenue = 0;


       for(Order order : orders) {
           if(order.getStatus().equals(OrderStatus.PRELIMINARY) ||
                   order.getStatus().equals(OrderStatus.CONFIRMED)|| order.getStatus().equals(OrderStatus.IN_PRODUCTION)){
               totalOrdersPreProduction += 1;
               if(order.getCurrency().equals(OrderCurrency.USD)) {
                     totalPreProductionRevenue += order.getTotalPrice() * 10.5;
                } else if(order.getCurrency().equals(OrderCurrency.EUR)) {
                     totalPreProductionRevenue += order.getTotalPrice() * 11;
                } else if(order.getCurrency().equals(OrderCurrency.MAD)) {
                     totalPreProductionRevenue += order.getTotalPrice();
               }
           }
           else if (order.getStatus().equals(OrderStatus.READY_TO_SHIPPED)
                   || order.getStatus().equals(OrderStatus.SHIPPED)) {
               totalOrdersPostProduction += 1;
               if(order.getCurrency().equals(OrderCurrency.USD)) {
                     totalPostProductionRevenue += order.getTotalPrice() * 10.5;
                } else if(order.getCurrency().equals(OrderCurrency.EUR)) {
                     totalPostProductionRevenue += order.getTotalPrice() * 11;
                } else if(order.getCurrency().equals(OrderCurrency.MAD)) {
                     totalPostProductionRevenue += order.getTotalPrice();
               }
           }
           else if (order.getStatus().equals(OrderStatus.RECEIVED)) {
               totalReceivedOrders += 1;
               if(order.getCurrency().equals(OrderCurrency.USD)) {
                     totalReceivedRevenue += order.getTotalPrice() * 10.5;
                } else if(order.getCurrency().equals(OrderCurrency.EUR)) {
                     totalReceivedRevenue += order.getTotalPrice() * 11;
                } else if(order.getCurrency().equals(OrderCurrency.MAD)) {
                     totalReceivedRevenue += order.getTotalPrice();
               }
           }
       }

       overviewDto.setTotalStock(totalStockWeight);
       overviewDto.setTotalOrders(totalOrders);
       overviewDto.setTotalOrdersPreProduction(totalOrdersPreProduction);
       overviewDto.setTotalOrdersPostProduction(totalOrdersPostProduction);
       overviewDto.setTotalReceivedOrders(totalReceivedOrders);

       overviewDto.setTotalPreProductionRevenue(totalPreProductionRevenue);
       overviewDto.setTotalPostProductionRevenue(totalPostProductionRevenue);
       overviewDto.setTotalReceivedOrdersRevenue(totalReceivedRevenue);

       overviewDto.setTotalRevenue(totalPreProductionRevenue + totalPostProductionRevenue + totalReceivedRevenue);
       return new ResponseEntity<>(overviewDto, HttpStatus.OK);
    }

    @Override
    @Cacheable(value = "marginPerClient", key = "#companyName")
    public ResponseEntity<MarginClientResponseDto> getMarginPerClient(String companyName) {
        MarginClientResponseDto response = clientMargin(companyName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @Override
    public ResponseEntity<?> getAllMarginPerClient() {
        return new ResponseEntity<>("",HttpStatus.OK);
    }

    private MarginClientResponseDto clientMargin(String companyName){
        Client client = clientRepository.findByCompanyName(companyName);
        if(client == null) {
            throw new ClientNotFoundException();
        }
        List<Order> clientOrders = orderRepository.findAllByClient(client);

        List<OrderCostDto> ordersCost = clientOrders
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

        double totalWeight = clientOrders.stream()
                .map(Order::getTotalWeight)
                .reduce(0.0, Double::sum);
        double totalRevenue = clientOrders.stream()
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
